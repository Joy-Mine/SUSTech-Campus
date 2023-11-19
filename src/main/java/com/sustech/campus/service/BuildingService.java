package com.sustech.campus.service;

import com.sustech.campus.entity.Building;

import java.util.List;

public interface BuildingService {

    /**
     * @return a List, which contains all buildings
     */
    List<Building> listAllBuildings();

    /**
     * @param name the name of the building
     * @return null if such building does not exist, otherwise a Building instance
     */
    Building getBuildingByName(String name);

    /**
     * @param name the name of the building
     * @return whether the building with the given name exists
     */
    boolean buildingExists(String name);

    /**
     * @param name
     * @param description
     * @param details
     * @param latitude
     * @param longitude
     * @return false if failed to add the given building, otherwise true
     */
    boolean addBuilding(String name, String description, String details, double latitude, double longitude);

    /**
     * @param name the name of the building
     * @return false if failed to delete the building, otherwise true
     */
    boolean deleteBuilding(String name);

    /**
     * @param name the name of the building
     * @param latitude
     * @param longitude
     * @return false if failed to change the location, otherwise true
     */
    boolean changeBuildingLocation(String name, double latitude, double longitude);

    /**
     * @param name the name of the building
     * @param description new description
     * @return false if failed to change the description, otherwise true
     */
    boolean changeBuildingDescription(String name, String description);

    /**
     * @param name the name of the building
     * @param details new details
     * @return false if failed to change the details, otherwise true
     */
    boolean changeBuildingDetails(String name, String details);
}
