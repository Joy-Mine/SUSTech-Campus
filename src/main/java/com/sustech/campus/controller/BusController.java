package com.sustech.campus.controller;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Station;
import com.sustech.campus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bus")
public class BusController {

    private final BusService busService;

    @Autowired
    public BusController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBusLine(@RequestBody Bus bus, @RequestBody List<String> stationNames) {
        boolean success = busService.addBusLine(bus.getName(), stationNames);
        if (success) {
            return ResponseEntity.ok("Bus line added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Bus line already exists or station not found.");
        }
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteBusLine(@PathVariable String name) {
        boolean success = busService.deleteBusLine(name);
        if (success) {
            return ResponseEntity.ok("Bus line deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bus line not found.");
        }
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<Bus>> listAllBusLines() {
        List<Bus> buses = busService.listAllBusLines();
        return ResponseEntity.ok(buses);
    }

    @GetMapping("/stations/{name}")
    public ResponseEntity<List<Station>> getStations(@PathVariable String name) {
        List<Station> stations = busService.getStations(name);
        if (stations != null && !stations.isEmpty()) {
            return ResponseEntity.ok(stations);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/updateStations/{name}")
    public ResponseEntity<String> setStations(@PathVariable String name, @RequestBody List<String> stationNames) {
        boolean success = busService.setStations(name, stationNames);
        if (success) {
            return ResponseEntity.ok("Bus line stations updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Bus line not found or station not valid.");
        }
    }
}
