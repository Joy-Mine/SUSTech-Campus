package com.sustech.campus.entity;

import java.util.List;

public class RouteInfo {
    Long routeId;
    String routeName;
    List<Station> stations;

    public RouteInfo(Long routeId, String routeName, List<Station> stations) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.stations = stations;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }
}
