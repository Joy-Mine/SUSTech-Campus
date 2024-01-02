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
     * @param buildingId
     * @return null if such building does not exist, otherwise a Building instance
     */
    Building getBuildingById(Long buildingId);

    /**
     * @param buildingName
     * @return null if such building does not exist, otherwise a Building instance
     */
    Building getBuildingByName(String buildingName);

    /**
     * @param buildingId
     * @return whether the building with the given id exists
     */
    boolean buildingExists(Long buildingId);

    /**
     * @param name
     * @param description
     * @param details
     * @param latitude
     * @param longitude
     * @return null if failed to add the given building, otherwise the id of the building
     */
    Long addBuilding(String name, String tag, String description, String details, double latitude, double longitude);

    /**
     * @param buildingId
     * @return false if failed to delete the building, otherwise true
     */
    boolean deleteBuilding(Long buildingId);

    boolean edit(long id, String name, String tag, String description, String details, double latitude, double longitude);

    /**
     * @param buildingId
     * @param newName
     * @return false if failed to change the name, otherwise true
     */
    boolean changeBuildingName(Long buildingId, String newName);

    /**
     * @param buildingId
     * @param newLatitude
     * @param newLongitude
     * @return false if failed to change the location, otherwise true
     */
    boolean changeBuildingLocation(Long buildingId, double newLatitude, double newLongitude);

    /**
     * @param buildingId
     * @param newDescription
     * @return false if failed to change the description, otherwise true
     */
    boolean changeBuildingDescription(Long buildingId, String newDescription);

    /**
     * @param buildingId
     * @param newDetails
     * @return false if failed to change the details, otherwise true
     */
    boolean changeBuildingDetails(Long buildingId, String newDetails);

    /**
     * @param buildingId
     * @param newTag
     * @return false if failed to change the tag, otherwise true
     */
    boolean changeBuildingTag(Long buildingId, String newTag);

    /**
     * @param buildingId
     * @return null if the building does not exist, otherwise, a List that contains all photos of the building
     */
    List<BuildingPhoto> listBuildingPhotos(Long buildingId);

    /**
     * @param photoId
     * @return null of the photo does not exist, otherwise, a photo instance
     */
    BuildingPhoto getBuildingPhotoById(Long photoId);

    /**
     * @param buildingId id of the building that the photo belongs to
     * @param path path of the photo
     * @return null if failed to add such photo, otherwise the id of the photo
     */
    Long addBuildingPhoto(Long buildingId, String path);

    /**
     * @param photoId
     * @return false if failed to delete the photo, otherwise true
     */
    boolean deleteBuildingPhoto(Long photoId);

    /**
     * @param photoId
     * @return whether the photo with the given id exists
     */
    boolean buildingPhotoExists(Long photoId);
}
