package com.sustech.campus.service;

import com.sustech.campus.entity.Station;

import java.util.List;

public interface RouteService {
    public List<Station> planRoute(long buildingId1, long buildingId2);
}
