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
        this.buildings = new ArrayList<>();
        this.buildingPhotos = new ArrayList<>();

    }

    @BeforeEach
    void insert() {
        int numBuildings = 4;
        int numBuildingPhotos = 10;
        for (int i = 0; i < numBuildings; ++i) {
            Building building = new Building();
            building.setName("test_building_" + i);
            building.setDetails("details_" + i);
            building.setDescription("description_" + i);
            building.setTag("tag_" + i);
            building.setLatitude(i);
            building.setLongitude(-i);
            Long buildingId = this.buildingService.addBuilding(
                    building.getName(),
                    building.getTag(),
                    building.getDescription(),
                    building.getDetails(),
                    building.getLatitude(),
                    building.getLongitude()
            );
            assertNotNull(buildingId);
            building.setId(buildingId);
            this.buildings.add(building);
        }
        for (int i = 0; i < numBuildingPhotos; ++i) {
            BuildingPhoto photo = new BuildingPhoto();
            Building building = this.buildings.get(new Random().nextInt(this.buildings.size()));
            photo.setBuildingId(building.getId());
            photo.setPath("/foo/bar/" + i);
            Long photoId = this.buildingService.addBuildingPhoto(
                    photo.getBuildingId(),
                    photo.getPath()
            );
            assertNotNull(photoId);
            photo.setId(photoId);
            this.buildingPhotos.add(photo);
        }
    }

    @AfterEach
    void clean() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertTrue(this.buildingService.deleteBuildingPhoto(photo.getId()));
        }
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.deleteBuilding(building.getId()));
        }
    }
    
    @Test
    @Order(1)
    void testListAllBuildings() {
        assertIterableEquals(
                this.buildingService.listAllBuildings().stream()
                        .sorted(Comparator.comparing(Building::getId)).toList(),
                this.buildings.stream().sorted(Comparator.comparing(Building::getId)).toList()
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
    void testGetBuildingById() {
        for (Building building : this.buildings) {
            assertEquals(
                    this.buildingService.getBuildingById(building.getId()),
                    building
            );
        }
    }

    @Test
    @Order(4)
    void testBuildingExists() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.buildingExists(building.getId()));
        }
    }
    
    @Test
    @Order(5)
    void testChangeBuildingLocation() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingLocation(
                    building.getId(),
                    building.getLatitude() + 1,
                    building.getLongitude() + 1
            ));
            building.setLongitude(building.getLongitude() + 1);
            building.setLatitude(building.getLatitude() + 1);
            assertEquals(
                    this.buildingService.getBuildingById(building.getId()),
                    building
            );
        }
    }

    @Test
    @Order(6)
    void testChangeBuildingDescription() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingDescription(
                    building.getId(),
                    "new description"
            ));
            building.setDescription("new description");
            assertEquals(
                    this.buildingService.getBuildingById(building.getId()),
                    building
            );
        }
    }

    @Test
    @Order(7)
    void testChangeBuildingDetails() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingDetails(
                    building.getId(),
                    "new details"
            ));
            building.setDetails("new details");
            assertEquals(
                    this.buildingService.getBuildingById(building.getId()),
                    building
            );
        }
    }

    @Test
    @Order(8)
    void testChangeBuildingName() {
        for (Building building : this.buildings) {
            assertTrue(this.buildingService.changeBuildingName(
                    building.getId(),
                    building.getName() + "_new"
            ));
            building.setName(building.getName() + "_new");
            assertEquals(
                    this.buildingService.getBuildingById(building.getId()),
                    building
            );
        }
    }

    @Test
    @Order(9)
    void testBuildingPhotoExists() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertTrue(this.buildingService.buildingPhotoExists(photo.getId()));
        }
    }

    @Test
    @Order(10)
    void testGetPhotoById() {
        for (BuildingPhoto photo : this.buildingPhotos) {
            assertEquals(
                    this.buildingService.getBuildingPhotoById(photo.getId()),
                    photo
            );
        }
    }

    @Test
    @Order(11)
    void testListBuildingPhotos() {
        for (Building building : this.buildings) {
            assertIterableEquals(
                    this.buildingService.listBuildingPhotos(building.getId()).stream()
                            .sorted(Comparator.comparing(BuildingPhoto::getId)).toList(),
                    this.buildingPhotos.stream().filter(e -> e.getBuildingId().equals(building.getId()))
                            .sorted(Comparator.comparing(BuildingPhoto::getId)).toList()
            );
        }
    }

    @Test
    @Order(12)
    void testChangeBuildingTag() {
        for (Building building : this.buildings) {
            building.setTag(building.getTag() + "_new");
            assertTrue(this.buildingService.changeBuildingTag(
                    building.getId(),
                    building.getTag()
            ));
            assertEquals(
                    this.buildingService.getBuildingById(building.getId()).getTag(),
                    building.getTag()
            );
        }
    }
}
