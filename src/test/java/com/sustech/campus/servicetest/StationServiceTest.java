package com.sustech.campus.servicetest;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Route;
import com.sustech.campus.entity.Station;
import com.sustech.campus.service.BusService;
import com.sustech.campus.service.StationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class StationServiceTest {
    private final BusService busService;

    private final StationService stationService;

    private List<Bus> buses;

    private List<Station> stations;

    private List<List<Route>> routes;

    @Autowired
    public StationServiceTest(BusService busService, StationService stationService) {
        this.busService = busService;
        this.stationService = stationService;
        this.stations = new ArrayList<>();
        this.buses = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

    @BeforeEach
    void insert() {
        int numBuses = 4;
        int numStations = 10;
        int numStationPerLine = 5;
        for (int i = 0; i < numStations; ++i) {
            Station station = new Station();
            station.setName("test_station_" + i);
            station.setLatitude(i);
            station.setLongitude(-i);
            Long stationId = this.stationService.addStation(
                    station.getName(),
                    station.getLatitude(),
                    station.getLongitude()
            );
            assertNotNull(stationId);
            station.setId(stationId);
            this.stations.add(station);
        }
        for (int i = 0; i < numBuses; ++i) {
            Bus bus = new Bus();
            bus.setName("test_bus_" + i);
            Long busId = this.busService.addBusLine(bus.getName());
            bus.setId(busId);
            this.buses.add(bus);

            ArrayList<Route> arrayList = new ArrayList<>();
            for (int j = 0; j < numStationPerLine; ++j) {
                int stationId = new Random().nextInt(numStations);
                Route route = new Route();
                route.setBusId(bus.getId());
                route.setStationId(this.stations.get(stationId).getId());
                route.setStopOrder(j);
                arrayList.add(route);
            }
            this.routes.add(arrayList);
            this.busService.changeBusStations(
                    this.buses.get(i).getId(),
                    this.routes.get(i).stream().map(Route::getStationId).toList()
            );
        }
    }

    @AfterEach
    void clean() {
        for (Bus bus : this.buses) {
            this.busService.deleteBusLine(bus.getId());
        }
        for (Station station : this.stations) {
            assertTrue(this.stationService.deleteStation(station.getId()));
        }
    }

    @Test
    @Order(1)
    void testStationExists() {
        for (Station station : this.stations) {
            assertTrue(this.stationService.stationExists(station.getId()));
        }
    }

    @Test
    @Order(2)
    void testListAllBusIds() {
        for (Station station : this.stations) {
            assertIterableEquals(
                    this.stationService.listAllBusIds(station.getId())
                            .stream().sorted().toList(),
                    Stream.iterate(0, e -> e < this.buses.size(), e -> e + 1)
                            .filter(
                                    e1 -> this.routes.get(e1).stream().map(Route::getStationId)
                                            .anyMatch(e2 -> e2.equals(station.getId()))
                            ).map(e -> this.buses.get(e).getId()).distinct().sorted().toList()
            );
        }
    }

    @Test
    @Order(3)
    void testListAllStations() {
        assertIterableEquals(
                this.stationService.listAllStations().stream().sorted(Comparator.comparing(Station::getName)).toList(),
                this.stations.stream().sorted(Comparator.comparing(Station::getName)).toList()
        );
    }

    @Test
    @Order(4)
    void testGetStationByName() {
        for (Station station : this.stations) {
            assertEquals(
                    this.stationService.getStationByName(station.getName()),
                    station
            );
        }
    }

    @Test
    @Order(5)
    void testGetStationById() {
        for (Station station : this.stations) {
            assertEquals(
                    this.stationService.getStationById(station.getId()),
                    station
            );
        }
    }

    @Test
    @Order(6)
    void testChangeStationLocation() {
        for (Station station : this.stations) {
            assertTrue(this.stationService.changeStationLocation(
                    station.getId(),
                    station.getLatitude() + 1,
                    station.getLongitude() + 1
            ));
            assertEquals(
                    this.stationService.getStationById(station.getId()).getLatitude(),
                    station.getLatitude() + 1
            );
            assertEquals(
                    this.stationService.getStationById(station.getId()).getLongitude(),
                    station.getLongitude() + 1
            );
        }
    }

    @Test
    @Order(7)
    void testChangeStationName() {
        for (Station station : this.stations) {
            assertTrue(this.stationService.changeStationName(
                    station.getId(),
                    station.getName() + "_new"
            ));
            station.setName(station.getName() + "_new");
            assertEquals(
                    this.stationService.getStationById(station.getId()),
                    station
            );
        }
    }
}
