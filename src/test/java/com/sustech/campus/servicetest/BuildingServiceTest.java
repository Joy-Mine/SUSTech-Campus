package com.sustech.campus.servicetest;

import com.sustech.campus.entity.Building;
import com.sustech.campus.service.BuildingService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuildingServiceTest {
    private final BuildingService buildingService;
    private final Building building;
    @Autowired
    public BuildingServiceTest(BuildingService buildingService) {
        this.buildingService = buildingService;
        this.building = new Building();
        this.building.setName("test_building");
        this.building.setDescription("this is description");
        this.building.setDetails("this is details");
        this.building.setLatitude(1);
        this.building.setLatitude(2);
    }

    @BeforeEach
    void clean() {
        this.buildingService.deleteBuilding(this.building.getName());
    }

    @Test
    @Order(1)
    void test() {
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
        assertNull(this.buildingService.getBuildingByName(this.building.getName()));

        assertTrue(this.buildingService.addBuilding(
                this.building.getName(),
                this.building.getDescription(),
                this.building.getDetails(),
                this.building.getLatitude(),
                this.building.getLongitude()
        ));
        assertNotNull(this.buildingService.getBuildingByName(this.building.getName()));
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getName(),
                this.building.getName()
        );
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getDescription(),
                this.building.getDescription()
        );
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getDetails(),
                this.building.getDetails()
        );
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getLatitude(),
                this.building.getLatitude()
        );
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getLongitude(),
                this.building.getLongitude()
        );

        assertTrue(this.buildingService.deleteBuilding(this.building.getName()));
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
        assertNull(this.buildingService.getBuildingByName(this.building.getName()));
    }

    @Test
    @Order(2)
    void testChangeBuildingDescription() {
        String newDescription = "this is the new description";
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
        assertTrue(this.buildingService.addBuilding(
                this.building.getName(),
                this.building.getDescription(),
                this.building.getDetails(),
                this.building.getLatitude(),
                this.building.getLongitude()
        ));

        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getDescription(),
                this.building.getDescription()
        );
        assertTrue(this.buildingService.changeBuildingDescription(
                this.building.getName(),
                newDescription
        ));
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getDescription(),
                newDescription
        );

        assertTrue(this.buildingService.deleteBuilding(this.building.getName()));
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
    }

    @Test
    @Order(3)
    void testChangeBuildingDetails() {
        String newDetails = "this is the new details";
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
        assertTrue(this.buildingService.addBuilding(
                this.building.getName(),
                this.building.getDescription(),
                this.building.getDetails(),
                this.building.getLatitude(),
                this.building.getLongitude()
        ));

        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getDetails(),
                this.building.getDetails()
        );
        assertTrue(this.buildingService.changeBuildingDetails(
                this.building.getName(),
                newDetails
        ));
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getDetails(),
                newDetails
        );

        assertTrue(this.buildingService.deleteBuilding(this.building.getName()));
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
    }

    @Test
    @Order(4)
    void testChangeBuildingLocation() {
        double newLatitude = 3, newLongitude = 4;
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
        assertTrue(this.buildingService.addBuilding(
                this.building.getName(),
                this.building.getDescription(),
                this.building.getDetails(),
                this.building.getLatitude(),
                this.building.getLongitude()
        ));

        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getLatitude(),
                this.building.getLatitude()
        );
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getLongitude(),
                this.building.getLongitude()
        );
        assertTrue(this.buildingService.changeBuildingLocation(
                this.building.getName(),
                newLatitude, newLongitude
        ));
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getLatitude(),
                newLatitude
        );
        assertEquals(
                this.buildingService.getBuildingByName(this.building.getName()).getLongitude(),
                newLongitude
        );

        assertTrue(this.buildingService.deleteBuilding(this.building.getName()));
        assertFalse(this.buildingService.buildingExists(this.building.getName()));
    }

    @Test
    @Order(5)
    void testListAllBuildings() {
        List<Building> buildings = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            Building building = new Building();
            building.setName("test_building_" + i);
            building.setDescription("description_" + i);
            building.setDetails("details_" + i);
            building.setLatitude(-i);
            building.setLongitude(i);
            buildings.add(building);
        }

        for (Building building : buildings) {
            assertFalse(this.buildingService.buildingExists(building.getName()));
            assertTrue(this.buildingService.addBuilding(
                    building.getName(),
                    building.getDescription(),
                    building.getDetails(),
                    building.getLatitude(),
                    building.getLongitude()
            ));
        }

        assertIterableEquals(
                this.buildingService.listAllBuildings().stream()
                        .sorted(Comparator.comparing(Building::getName)).toList(),
                buildings.stream().sorted(Comparator.comparing(Building::getName)).toList()
        );

        for (Building building : buildings) {
            assertTrue(this.buildingService.deleteBuilding(building.getName()));
            assertFalse(this.buildingService.buildingExists(building.getName()));
        }
    }
}
