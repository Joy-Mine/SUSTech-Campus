package com.sustech.campus.serviceImpl;

import com.sustech.campus.entity.Building;
import com.sustech.campus.mapper.BuildingMapper;
import com.sustech.campus.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingServiceImpl implements BuildingService {

    private BuildingMapper buildingMapper;

    @Autowired
    public BuildingServiceImpl(BuildingMapper buildingMapper) {
        this.buildingMapper = buildingMapper;
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
}
