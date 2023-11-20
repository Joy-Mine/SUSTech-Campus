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
    public Building getBuildingByName(String name) {
        return buildingMapper.selectById(name);
    }

    @Override
    public boolean buildingExists(String name) {
        return this.getBuildingByName(name) != null;
    }

    @Override
    public boolean addBuilding(String name, String description, String details, double latitude, double longitude) {
        if (this.buildingExists(name)) {
            return false;
        }
        Building building = new Building();
        building.setName(name);
        building.setDescription(description);
        building.setDetails(details);
        building.setLatitude(latitude);
        building.setLongitude(longitude);
        this.buildingMapper.insert(building);
        return true;
    }

    @Override
    public boolean deleteBuilding(String name) {
        if (!this.buildingExists(name)) {
            return false;
        }
        QueryWrapper<BuildingPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("building", name);
        this.buildingPhotoMapper.delete(wrapper);
        this.buildingMapper.deleteById(name);
        return true;
    }

    @Override
    public boolean changeBuildingLocation(String name, double latitude, double longitude) {
        Building building = this.getBuildingByName(name);
        if (building == null) {
            return false;
        }
        building.setLatitude(latitude);
        building.setLongitude(longitude);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingDescription(String name, String description) {
        Building building = this.getBuildingByName(name);
        if (building == null) {
            return false;
        }
        building.setDescription(description);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public boolean changeBuildingDetails(String name, String details) {
        Building building = this.getBuildingByName(name);
        if (building == null) {
            return false;
        }
        building.setDetails(details);
        this.buildingMapper.updateById(building);
        return true;
    }

    @Override
    public List<BuildingPhoto> listBuildingPhotos(String buildingName) {
        if (this.buildingMapper.selectById(buildingName) == null) {
            return null;
        }
        QueryWrapper<BuildingPhoto> wrapper = new QueryWrapper<>();
        wrapper.eq("building", buildingName);
        return this.buildingPhotoMapper.selectList(wrapper);
    }

    @Override
    public BuildingPhoto getBuildingPhotoById(Long id) {
        return this.buildingPhotoMapper.selectById(id);
    }

    @Override
    public Long addBuildingPhoto(String buildingName, String path) {
        if (this.buildingMapper.selectById(buildingName) == null) {
            return null;
        }
        BuildingPhoto photo = new BuildingPhoto();
        photo.setBuilding(buildingName);
        photo.setPath(path);
        this.buildingPhotoMapper.insert(photo);
        return photo.getId();
    }

    @Override
    public boolean deleteBuildingPhoto(Long id) {
        if (!this.buildingPhotoExists(id)) {
            return false;
        }
        this.buildingPhotoMapper.deleteById(id);
        return true;
    }

    @Override
    public boolean buildingPhotoExists(Long id) {
        return this.getBuildingPhotoById(id) != null;
    }

}
