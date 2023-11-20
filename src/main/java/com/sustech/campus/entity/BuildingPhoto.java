package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("building_photo")
public class BuildingPhoto {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String building;

    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BuildingPhoto other)) {
            return false;
        }
        return obj == this
                || this.id.equals(other.id)
                && this.building.equals(other.building)
                && this.path.equals(other.path);
    }

    @Override
    public String toString() {
        return "BuildingPhoto{" +
                "id=" + id +
                ", building='" + building + "'" +
                ", path='" + path + "'" +
                '}';
    }
}
