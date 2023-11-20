package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("station")
public class Station {
    @TableId(type = IdType.INPUT)
    private String name;

    private double latitude, longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Station other)) {
            return false;
        }
        return this == other
                || this.name.equals(other.name)
                && this.latitude == other.latitude
                && this.longitude == other.longitude;
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + this.name + "'" +
                ", latitude=" + this.latitude +
                ", longitude=" + this.longitude +
                '}';
    }
}