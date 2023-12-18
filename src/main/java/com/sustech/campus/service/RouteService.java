package com.sustech.campus.service;

import java.util.List;

public interface RouteService {
    public List<Long> planRoute(long buildingId1, long buildingId2);
}
