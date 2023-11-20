package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.BuildingPhoto;
import com.sustech.campus.mapper.BuildingMapper;
import com.sustech.campus.mapper.BuildingPhotoMapper;
import com.sustech.campus.service.BuildingPhotoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuildingPhotoServiceImpl implements BuildingPhotoService {
    private BuildingMapper buildingMapper;

    private BuildingPhotoMapper buildingPhotoMapper;

    public BuildingPhotoServiceImpl(BuildingMapper buildingMapper, BuildingPhotoMapper buildingPhotoMapper) {
        this.buildingMapper = buildingMapper;
        this.buildingPhotoMapper = buildingPhotoMapper;
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
    public BuildingPhoto getPhotoById(Long id) {
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
        return this.getPhotoById(id) != null;
    }
}
