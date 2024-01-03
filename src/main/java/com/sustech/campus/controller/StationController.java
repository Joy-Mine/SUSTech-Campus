package com.sustech.campus.controller;

import com.sustech.campus.entity.Station;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/station")
public class StationController {

    private final StationService stationService;

    @Autowired
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = stationService.listAllStations();
        return ResponseEntity.ok(stations);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Station> getStation(@PathVariable String name) {
        Station station = stationService.getStationByName(name);
        if (station != null) {
            return ResponseEntity.ok(station);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<String> addStation(@RequestBody Station station) {
        Long stationId = stationService.addStation(station.getName(), station.getLatitude(), station.getLongitude());
        if (stationId != null) {
            return ResponseEntity.ok("Station added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Station already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/edit")
    public boolean editStation(@RequestBody Station station) {
        System.out.println(station.toString());
        boolean success = stationService.editStation(station.getId(),station.getName(),station.getLatitude(), station.getLongitude());
        return success;
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStation(@PathVariable long id) {
        boolean success = stationService.deleteStation(id);
        if (success) {
            return ResponseEntity.ok("Station deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Station not found or cannot be deleted.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateLocation/{id}")
    public ResponseEntity<String> updateStationLocation(@PathVariable long id, @RequestParam double latitude, @RequestParam double longitude) {
        boolean success = stationService.changeStationLocation(id, latitude, longitude);
        if (success) {
            return ResponseEntity.ok("Station location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Station not found.");
        }
    }

    @Access(level = UserType.USER)
    @GetMapping("/listBusLines/{id}")
    public ResponseEntity<List<Long>> listAllBusLines(@PathVariable long id) {
        List<Long> buses = stationService.listAllBusIds(id);
        if (buses != null) {
            return ResponseEntity.ok(buses);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
