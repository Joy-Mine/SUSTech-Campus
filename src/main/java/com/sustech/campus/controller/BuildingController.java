package com.sustech.campus.controller;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;
import com.sustech.campus.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService buildingService;

    @Autowired
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Building>> getAllBuildings() {
        List<Building> buildings = buildingService.listAllBuildings();
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Building> getBuildingByName(@PathVariable String name) {
        Building building = buildingService.getBuildingByName(name);
        if (building != null) {
            return ResponseEntity.ok(building);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<String> addBuilding(@RequestBody Building building) {
        boolean success = buildingService.addBuilding(building.getName(), building.getDescription(), building.getDetails(), building.getLatitude(), building.getLongitude());
        if (success) {
            return ResponseEntity.ok("Building added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Building already exists.");
        }
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteBuilding(@PathVariable String name) {
        boolean success = buildingService.deleteBuilding(name);
        if (success) {
            return ResponseEntity.ok("Building deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @PutMapping("/updateLocation/{name}")
    public ResponseEntity<String> changeBuildingLocation(@PathVariable String name, @RequestParam double latitude, @RequestParam double longitude) {
        boolean success = buildingService.changeBuildingLocation(name, latitude, longitude);
        if (success) {
            return ResponseEntity.ok("Building location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @PutMapping("/updateDescription/{name}")
    public ResponseEntity<String> changeBuildingDescription(@PathVariable String name, @RequestBody String description) {
        boolean success = buildingService.changeBuildingDescription(name, description);
        if (success) {
            return ResponseEntity.ok("Building description updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @GetMapping("/photos/{buildingName}")
    public ResponseEntity<List<BuildingPhoto>> listBuildingPhotos(@PathVariable String buildingName) {
        List<BuildingPhoto> photos = buildingService.listBuildingPhotos(buildingName);
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}