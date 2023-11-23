package com.sustech.campus.controller;

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
        Station station = stationService.getStationByName(name);
        if (station != null) {
            return ResponseEntity.ok(station);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addStation(@RequestBody Station station) {
        Long stationId = stationService.addStation(station.getName(), station.getLatitude(), station.getLongitude());
        if (stationId != null) {
            return ResponseEntity.ok("Station added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Station already exists.");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStation(@PathVariable long id) {
        boolean success = stationService.deleteStation(id);
        if (success) {
            return ResponseEntity.ok("Station deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Station not found or cannot be deleted.");
        }
    }

    @PutMapping("/updateLocation/{id}")
    public ResponseEntity<String> updateStationLocation(@PathVariable long id, @RequestParam double latitude, @RequestParam double longitude) {
        boolean success = stationService.changeStationLocation(id, latitude, longitude);
        if (success) {
            return ResponseEntity.ok("Station location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Station not found.");
        }
    }

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
