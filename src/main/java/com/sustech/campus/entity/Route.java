package com.sustech.campus.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("route")
public class Route {
    private String bus;

    private String station;

    private int order;

    public String getBus() {
        return bus;
    }

    public void setBus(String bus) {
        this.bus = bus;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
