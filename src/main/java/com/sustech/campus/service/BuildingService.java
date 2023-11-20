package com.sustech.campus.service;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;

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
     * @param name      the name of the building
     * @param latitude
     * @param longitude
     * @return false if failed to change the location, otherwise true
     */
    boolean changeBuildingLocation(String name, double latitude, double longitude);

    /**
     * @param name        the name of the building
     * @param description new description
     * @return false if failed to change the description, otherwise true
     */
    boolean changeBuildingDescription(String name, String description);

    /**
     * @param name    the name of the building
     * @param details new details
     * @return false if failed to change the details, otherwise true
     */
    boolean changeBuildingDetails(String name, String details);

    /**
     * @param buildingName name of the building
     * @return null if the building does not exist, otherwise, a List that contains all photos of the building
     */
    List<BuildingPhoto> listBuildingPhotos(String buildingName);

    /**
     * @param id
     * @return null of the photo does not exist, otherwise, a photo instance
     */
    BuildingPhoto getBuildingPhotoById(Long id);

    /**
     * @param buildingName name of the building that the photo belongs to
     * @param path path of the photo
     * @return null if failed to add such photo, otherwise the id of the photo
     */
    Long addBuildingPhoto(String buildingName, String path);

    /**
     * @param id id of the photo
     * @return false if failed to delete the photo, otherwise true
     */
    boolean deleteBuildingPhoto(Long id);

    /**
     * @param id id of the photo
     * @return whether the photo with the given id exists
     */
    boolean buildingPhotoExists(Long id);
}
