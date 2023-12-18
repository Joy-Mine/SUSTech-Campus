package com.sustech.campus.controller;

import com.sustech.campus.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/planRoute")
    public ResponseEntity<List<Long>> planRoute(@RequestParam long buildingId1, @RequestParam long buildingId2) {
        List<Long> routePlan = routeService.planRoute(buildingId1, buildingId2);
        if (routePlan != null && !routePlan.isEmpty()) {
            return ResponseEntity.ok(routePlan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
