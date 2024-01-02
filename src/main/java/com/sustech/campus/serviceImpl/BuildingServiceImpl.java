package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.BuildingPhoto;
import com.sustech.campus.mapper.BuildingMapper;
import com.sustech.campus.mapper.BuildingPhotoMapper;
import com.sustech.campus.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private BuildingMapper buildingMapper;

    private BuildingPhotoMapper buildingPhotoMapper;

    @Autowired
    public BuildingServiceImpl(BuildingMapper buildingMapper, BuildingPhotoMapper buildingPhotoMapper) {
        this.buildingMapper = buildingMapper;
        this.buildingPhotoMapper = buildingPhotoMapper;
    }

    @Override
    public List<Building> listAllBuildings() {
        return buildingMapper.selectList(null);
    }

    @Override
    public Building getBuildingById(Long buildingId) {
        return buildingMapper.selectById(buildingId);
    }

    @Override
    public Building getBuildingByName(String buildingName) {
        QueryWrapper<Building> wrapper = new QueryWrapper<>();
        wrapper.eq("name", buildingName);
        return buildingMapper.selectOne(wrapper);
    }

    @Override
    public boolean buildingExists(Long buildingId) {
        return this.getBuildingById(buildingId) != null;
    }

    @Override
    public Long addBuilding(String name, String tag, String description, String details, double latitude, double longitude) {
        if (this.getBuildingByName(name) != null) {
            return null;
        }
        Building building = new Building();
        building.setName(name);
        building.setTag(tag);
        building.setDescription(description);
        building.setDetails(details);
        building.setLatitude(latitude);
        building.setLongitude(longitude);
        this.buildingMapper.insert(building);
        return building.getId();
    }

    @Override
    public boolean deleteBuilding(Long buildingId) {
        if (!this.buildingExists(buildingId)) {
            return false;
        }
        QueryWrapper<BuildingPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("buildingId", buildingId);
        this.buildingPhotoMapper.delete(wrapper);
        this.buildingMapper.deleteById(buildingId);
        return true;
    }

    @Override
    public boolean edit(long id, String name, String tag, String description, String details, double latitude, double longitude) {
        Building building=this.getBuildingById(id);
        if(building==null)
            return false;
        building.setName(name);
        building.setTag(tag);
        building.setDescription(description);
        building.setDetails(details);
        building.setLatitude(latitude);
        building.setLongitude(longitude);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingName(Long buildingId, String newName) {
        Building building = this.getBuildingById(buildingId);
        if (building == null) {
            return false;
        }
        building.setName(newName);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingLocation(Long buildingId, double newLatitude, double newLongitude) {
        Building building = this.getBuildingById(buildingId);
        if (building == null) {
            return false;
        }
        building.setLatitude(newLatitude);
        building.setLongitude(newLongitude);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingDescription(Long buildingId, String newDescription) {
        Building building = this.getBuildingById(buildingId);
        if (building == null) {
            return false;
        }
        building.setDescription(newDescription);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingDetails(Long buildingId, String newDetails) {
        Building building = this.getBuildingById(buildingId);
        if (building == null) {
            return false;
        }
        building.setDetails(newDetails);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingTag(Long buildingId, String newTag) {
        Building building = this.getBuildingById(buildingId);
        if (building == null) {
            return false;
        }
        building.setTag(newTag);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public List<BuildingPhoto> listBuildingPhotos(Long buildingId) {
        if (!this.buildingExists(buildingId)) {
            return null;
        }
        QueryWrapper<BuildingPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("buildingId", buildingId);
        return this.buildingPhotoMapper.selectList(wrapper);
    }

    @Override
    public BuildingPhoto getBuildingPhotoById(Long photoId) {
        return this.buildingPhotoMapper.selectById(photoId);
    }

    @Override
    public Long addBuildingPhoto(Long buildingId, String path) {
        if (!this.buildingExists(buildingId)) {
            return null;
        }
        BuildingPhoto photo = new BuildingPhoto();
        photo.setBuildingId(buildingId);
        photo.setPath(path);
        this.buildingPhotoMapper.insert(photo);
        return photo.getId();
    }

    @Override
    public boolean deleteBuildingPhoto(Long photoId) {
        if (!this.buildingPhotoExists(photoId)) {
            return false;
        }
        this.buildingPhotoMapper.deleteById(photoId);
        return true;
    }

    @Override
    public boolean buildingPhotoExists(Long photoId) {
        return this.getBuildingPhotoById(photoId) != null;
    }
}
