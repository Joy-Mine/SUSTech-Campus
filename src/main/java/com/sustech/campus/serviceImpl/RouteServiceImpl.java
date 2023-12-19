package com.sustech.campus.serviceImpl;

import com.sustech.campus.entity.Building;
import com.sustech.campus.entity.Station;
import com.sustech.campus.service.BuildingService;
import com.sustech.campus.service.RouteService;
import com.sustech.campus.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {

    private final BuildingService buildingService;
    private final StationService stationService;

    @Autowired
    public RouteServiceImpl(BuildingService buildingService, StationService stationService) {
        this.buildingService = buildingService;
        this.stationService = stationService;
    }

    @Override
    public List<Station> planRoute(long buildingId1, long buildingId2) {
        Building building1 = buildingService.getBuildingById(buildingId1);
        Building building2 = buildingService.getBuildingById(buildingId2);

        Station nearestStation1 = stationService.findNearestStation(building1.getLatitude(), building1.getLongitude());
        Station nearestStation2 = stationService.findNearestStation(building2.getLatitude(), building2.getLongitude());

        return stationService.findRouteBetweenStations(nearestStation1.getId(), nearestStation2.getId());
    }
}
