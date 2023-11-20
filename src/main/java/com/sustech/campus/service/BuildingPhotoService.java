package com.sustech.campus.service;

import com.sustech.campus.entity.BuildingPhoto;

import java.util.List;

public interface BuildingPhotoService {
    /**
     * @param buildingName name of the building
     * @return null if the building does not exist, otherwise, a List that contains all photos of the building
     */
    List<BuildingPhoto> listBuildingPhotos(String buildingName);

    /**
     * @param id
     * @return null of the photo does not exist, otherwise, a photo instance
     */
    BuildingPhoto getPhotoById(Long id);

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
