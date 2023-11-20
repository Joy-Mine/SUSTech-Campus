package com.sustech.campus.servicetest;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;
import com.sustech.campus.service.BuildingPhotoService;
import com.sustech.campus.service.BuildingService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuildingPhotoServiceTest {
    private final BuildingService buildingService;

    private final BuildingPhotoService buildingPhotoService;

    private List<Building> buildings;

    private List<BuildingPhoto> buildingPhotos;

    @Autowired
    public BuildingPhotoServiceTest(BuildingService buildingService, BuildingPhotoService buildingPhotoService) {
        this.buildingService = buildingService;
        this.buildingPhotoService = buildingPhotoService;
        int numBuildings = 4;
        int numBuildingPhotos = 10;
        this.buildings = new ArrayList<>();
        this.buildingPhotos = new ArrayList<>();
        for (int i = 0; i < numBuildings; ++i) {
            Building building = new Building();
            building.setName("test_building_" + i);
            building.setDetails("");
            building.setDescription("");
            building.setLatitude(0);
            building.setLongitude(0);
            this.buildings.add(building);
        }
        for (int i = 0; i <numBuildingPhotos; ++i) {
            BuildingPhoto photo = new BuildingPhoto();
            photo.setBuilding("test_building_" + new Random().nextInt(numBuildings));
            photo.setPath("/foo/bar/" + i);
            this.buildingPhotos.add(photo);
        }
    }

    @BeforeEach
    void insert() {
        for (Building building : this.buildings) {
            this.buildingService.addBuilding(
                    building.getName(),
                    building.getDescription(),
                    building.getDetails(),
                    building.getLatitude(),
                    building.getLongitude()
            );
        }
        for (BuildingPhoto buildingPhoto : this.buildingPhotos) {
            Long result = this.buildingPhotoService.addBuildingPhoto(
                    buildingPhoto.getBuilding(),
                    buildingPhoto.getPath()
            );
            assertNotNull(result);
            buildingPhoto.setId(result);
        }
    }

    @AfterEach
    void clean() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertTrue(this.buildingPhotoService.deleteBuildingPhoto(photo.getId()));
        }
        for (Building building : this.buildings) {
            this.buildingService.deleteBuilding(building.getName());
        }
    }

    @Test
    @Order(1)
    void testBuildingPhotoExists() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertTrue(this.buildingPhotoService.buildingPhotoExists(photo.getId()));
        }
    }

    @Test
    @Order(2)
    void testGetPhotoById() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertEquals(
                    this.buildingPhotoService.getPhotoById(photo.getId()),
                    photo
            );
        }
    }

    @Test
    @Order(3)
    void testListBuildingPhotos() {
        for (Building building : this.buildings) {
            assertIterableEquals(
                    this.buildingPhotoService.listBuildingPhotos(building.getName()).stream()
                            .sorted(Comparator.comparing(BuildingPhoto::getId)).toList(),
                    this.buildingPhotos.stream().filter(e -> e.getBuilding().equals(building.getName()))
                            .sorted(Comparator.comparing(BuildingPhoto::getId)).toList()
            );
        }
    }
}
