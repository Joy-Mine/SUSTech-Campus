package com.sustech.campus.service;

import com.sustech.campus.entity.RouteInfo;
import com.sustech.campus.entity.Station;

import java.util.List;

public interface RouteService {
    List<Station> planRoute(long buildingId1, long buildingId2);

    List<RouteInfo> getAllRoutes();
}
