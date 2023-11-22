package com.sustech.campus.controller;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Station;
import com.sustech.campus.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
        Station station = stationService.getStation(name);
        if (station != null) {
            return ResponseEntity.ok(station);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStation(@RequestBody Station station) {
        boolean success = stationService.addStation(station.getName(), station.getLatitude(), station.getLongitude());
        if (success) {
            return ResponseEntity.ok("Station added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Station already exists.");
        }
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteStation(@PathVariable String name) {
        boolean success = stationService.deleteStation(name);
        if (success) {
            return ResponseEntity.ok("Station deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Station not found or cannot be deleted.");
        }
    }

    @PutMapping("/updateLocation/{name}")
    public ResponseEntity<String> updateStationLocation(@PathVariable String name, @RequestParam double latitude, @RequestParam double longitude) {
        boolean success = stationService.changeStationLocation(name, latitude, longitude);
        if (success) {
            return ResponseEntity.ok("Station location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Station not found.");
        }
    }

    @GetMapping("/listBusLines/{name}")
    public ResponseEntity<List<Bus>> listAllBusLines(@PathVariable String name) {
        List<Bus> buses = stationService.listAllBusLines(name);
        if (buses != null) {
            return ResponseEntity.ok(buses);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
