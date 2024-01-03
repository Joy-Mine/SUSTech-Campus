package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("building_photo")
public class BuildingPhoto {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "buildingId")
    private Long buildingId;

    private String path;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
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
                && this.buildingId.equals(other.buildingId)
                && this.path.equals(other.path);
    }

    @Override
    public String toString() {
        return "BuildingPhoto{" +
                "id=" + id +
                ", buildingId=" + buildingId +
                ", path='" + path + '\'' +
                '}';
    }
}
