package com.sustech.campus.controller;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;
import com.sustech.campus.enums.UserType;
import com.sustech.campus.interceptor.Access;
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

    @Access(level = UserType.ADMIN)
    @PostMapping("/add")
    public ResponseEntity<String> addBuilding(@RequestBody Building building) {
        Long buildingId = buildingService.addBuilding(building.getName(), building.getTag(), building.getDescription(), building.getDetails(), building.getLatitude(), building.getLongitude());
        if (buildingId != null) {
            return ResponseEntity.ok("Building added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Building already exists.");
        }
    }

    @Access(level = UserType.ADMIN)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBuilding(@PathVariable long id) {
        boolean success = buildingService.deleteBuilding(id);
        if (success) {
            return ResponseEntity.ok("Building deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateLocation/{id}")
    public ResponseEntity<String> changeBuildingLocation(@PathVariable long id, @RequestParam double latitude, @RequestParam double longitude) {
        boolean success = buildingService.changeBuildingLocation(id, latitude, longitude);
        if (success) {
            return ResponseEntity.ok("Building location updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @Access(level = UserType.ADMIN)
    @PutMapping("/updateDescription/{id}")
    public ResponseEntity<String> changeBuildingDescription(@PathVariable long id, @RequestBody String description) {
        boolean success = buildingService.changeBuildingDescription(id, description);
        if (success) {
            return ResponseEntity.ok("Building description updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Building not found.");
        }
    }

    @GetMapping("/photos/{buildingId}")
    public ResponseEntity<List<BuildingPhoto>> listBuildingPhotos(@PathVariable long buildingId) {
        List<BuildingPhoto> photos = buildingService.listBuildingPhotos(buildingId);
        if (photos != null && !photos.isEmpty()) {
            return ResponseEntity.ok(photos);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}