package com.sustech.campus.controller;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Station;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
import com.sustech.campus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/bus")
public class BusController {

    private final BusService busService;

    @Autowired
    public BusController(BusService busService) {
        this.busService = busService;
    }

    @Access(level = UserType.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<String> addBusLine(@RequestBody Bus bus) {
        Long busId = busService.addBusLine(bus.getName());
        if (busId != null) {
            return ResponseEntity.ok("Bus line added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Bus line already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBusLine(@PathVariable Long id) {
        boolean success = busService.deleteBusLine(id);
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

    @GetMapping("/stations/{id}")
    public ResponseEntity<List<Station>> getStations(@PathVariable Long id) {
        List<Station> stations = busService.getStations(id);
        if (stations != null && !stations.isEmpty()) {
            return ResponseEntity.ok(stations);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateStations/{id}")
    public ResponseEntity<String> setStations(@PathVariable Long id, @RequestBody List<Long> stationIds) {
        boolean success = busService.changeBusStations(id, stationIds);
        if (success) {
            return ResponseEntity.ok("Bus line stations updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Bus line not found or station not valid.");
        }
    }
}
