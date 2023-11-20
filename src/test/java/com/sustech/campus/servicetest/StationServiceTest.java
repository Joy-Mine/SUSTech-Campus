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
        int numBuses = 4;
        int numStations = 10;
        int numStationPerLine = 5;
        for (int i = 0; i < numStations; ++i) {
            Station station = new Station();
            station.setName("test_station_" + i);
            station.setLatitude(i);
            station.setLongitude(-i);
            this.stations.add(station);
        }
        for (int i = 0; i < numBuses; ++i) {
            Bus bus = new Bus();
            bus.setName("test_bus_" + i);
            this.buses.add(bus);
            ArrayList<Route> arrayList = new ArrayList<>();
            for (int j = 0; j < numStationPerLine; ++j) {
                int stationId = new Random().nextInt(numStations);
                Route route = new Route();
                route.setBus(bus.getName());
                route.setStation(this.stations.get(stationId).getName());
                route.setStopOrder(j);
                arrayList.add(route);
            }
            this.routes.add(arrayList);
        }
    }

    @BeforeEach
    void insert() {
        for (Station station : this.stations) {
            assertTrue(this.stationService.addStation(
                    station.getName(),
                    station.getLatitude(),
                    station.getLongitude()
            ));
        }
        for (int i = 0; i < this.buses.size(); ++i) {
            assertTrue(this.busService.addBusLine(
                    this.buses.get(i).getName(),
                    this.routes.get(i).stream().map(Route::getStation).toList()
            ));
        }
    }

    @AfterEach
    void clean() {
        for (Bus bus : this.buses) {
            assertTrue(this.busService.deleteBusLine(bus.getName()));
        }
        for (Station station : this.stations) {
            assertTrue(this.stationService.deleteStation(station.getName()));
        }
    }

    @Test
    @Order(1)
    void testStationExists() {
        for (Station station : this.stations) {
            assertTrue(this.stationService.stationExists(station.getName()));
        }
    }

    @Test
    @Order(2)
    void testListAllBusLines() {
        for (Station station : this.stations) {
            assertIterableEquals(
                    this.stationService.listAllBusLines(station.getName())
                            .stream().sorted(Comparator.comparing(Bus::getName)).toList(),
                    Stream.iterate(0, e -> e < this.buses.size(), e -> e + 1)
                            .filter(
                                    e1 -> this.routes.get(e1).stream().map(Route::getStation)
                                            .anyMatch(e2 -> e2.equals(station.getName()))
                            ).map(e -> this.buses.get(e)).toList()
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
    void testGetStations() {
        for (Station station : this.stations) {
            assertEquals(
                    this.stationService.getStation(station.getName()),
                    station
            );
        }
    }

    @Test
    @Order(5)
    void testChangeStationLocation() {
        for (Station station : this.stations) {
            assertTrue(this.stationService.changeStationLocation(
                    station.getName(),
                    station.getLatitude() + 1,
                    station.getLongitude() + 1
            ));
            assertEquals(
                    this.stationService.getStation(station.getName()).getLatitude(),
                    station.getLatitude() + 1
            );
            assertEquals(
                    this.stationService.getStation(station.getName()).getLongitude(),
                    station.getLongitude() + 1
            );
        }
    }
}
