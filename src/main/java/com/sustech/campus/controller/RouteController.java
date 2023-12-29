package com.sustech.campus.controller;

import com.sustech.campus.entity.Station;
import com.sustech.campus.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/planRoute")
    public ResponseEntity<List<double[]>> planRoute(@RequestParam long buildingId1, @RequestParam long buildingId2) {
        System.out.println(buildingId1);
        System.out.println(buildingId2);
        List<Station> routePlan = routeService.planRoute(buildingId1, buildingId2);
        if (routePlan != null && !routePlan.isEmpty()) {
            System.out.println(routePlan);
            List<double[]> ans=new ArrayList<>();
            for(Station station : routePlan)
                ans.add(new double[]{station.getLongitude(),station.getLatitude()});
            for (double[] array : ans)
                System.out.println(Arrays.toString(array));
            System.out.println(buildingId1);
            System.out.println(buildingId2);
            return ResponseEntity.ok(ans);
        } else {
            System.out.println("not route");
            return ResponseEntity.notFound().build();
        }
    }
}
