package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("building")
public class Building {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String tag;

    private String description, details;

    private double latitude, longitude;

    @TableField(exist = false)
    private String coverPath;

    public String getCoverPath(){return coverPath;}

    public void setCoverPath(String coverPath){
        this.coverPath=coverPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag(){
        return tag;
    }

    public void setTag(String tag){
        this.tag=tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Building other)) {
            return false;
        }
        return this == other
                || this.id.equals(other.id)
                && this.name.equals(other.name)
                && this.tag.equals(other.tag)
                && this.description.equals(other.description)
                && this.details.equals(other.details)
                && this.latitude == other.latitude
                && this.longitude == other.longitude;
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tag='" + tag +'\''+
                ", description='" + description + '\'' +
                ", details='" + details + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
