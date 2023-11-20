package com.sustech.campus.servicetest;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;
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
public class BuildingServiceTest {
    private final BuildingService buildingService;

    private List<Building> buildings;

    private List<BuildingPhoto> buildingPhotos;

    @Autowired
    public BuildingServiceTest(BuildingService buildingService) {
        this.buildingService = buildingService;
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
            assertTrue(this.buildingService.addBuilding(
                    building.getName(),
                    building.getDescription(),
                    building.getDetails(),
                    building.getLatitude(),
                    building.getLongitude()
            ));
        }
        for (BuildingPhoto buildingPhoto : this.buildingPhotos) {
            Long result = this.buildingService.addBuildingPhoto(
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
            assertTrue(this.buildingService.deleteBuildingPhoto(photo.getId()));
        }
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.deleteBuilding(building.getName()));
        }
    }
    
    @Test
    @Order(1)
    void testListAllBuildings() {
        assertIterableEquals(
                this.buildingService.listAllBuildings().stream()
                        .sorted(Comparator.comparing(Building::getName)).toList(),
                this.buildings.stream().sorted(Comparator.comparing(Building::getName)).toList()
        );
    }
    
    @Test
    @Order(2)
    void testGetBuildingByName() {
        for (Building building : this.buildings) {
            assertEquals(
                    this.buildingService.getBuildingByName(building.getName()),
                    building
            );
        }
    }
    
    @Test
    @Order(3)
    void testBuildingExists() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.buildingExists(building.getName()));
        }
    }
    
    @Test
    @Order(4)
    void testChangeBuildingLocation() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingLocation(
                    building.getName(),
                    building.getLatitude() + 1,
                    building.getLongitude() + 1
            ));
            building.setLongitude(building.getLongitude() + 1);
            building.setLatitude(building.getLatitude() + 1);
            assertEquals(
                    this.buildingService.getBuildingByName(building.getName()),
                    building
            );
        }
    }

    @Test
    @Order(5)
    void testChangeBuildingDescription() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingDescription(
                    building.getName(),
                    "new description"
            ));
            building.setDescription("new description");
            assertEquals(
                    this.buildingService.getBuildingByName(building.getName()),
                    building
            );
        }
    }

    @Test
    @Order(6)
    void testChangeBuildingDetails() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingDetails(
                    building.getName(),
                    "new details"
            ));
            building.setDetails("new details");
            assertEquals(
                    this.buildingService.getBuildingByName(building.getName()),
                    building
            );
        }
    }

    @Test
    @Order(7)
    void testBuildingPhotoExists() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertTrue(this.buildingService.buildingPhotoExists(photo.getId()));
        }
    }

    @Test
    @Order(8)
    void testGetPhotoById() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertEquals(
                    this.buildingService.getBuildingPhotoById(photo.getId()),
                    photo
            );
        }
    }

    @Test
    @Order(9)
    void testListBuildingPhotos() {
        for (Building building : this.buildings) {
            assertIterableEquals(
                    this.buildingService.listBuildingPhotos(building.getName()).stream()
                            .sorted(Comparator.comparing(BuildingPhoto::getId)).toList(),
                    this.buildingPhotos.stream().filter(e -> e.getBuilding().equals(building.getName()))
                            .sorted(Comparator.comparing(BuildingPhoto::getId)).toList()
            );
        }
    }
    
    
}
