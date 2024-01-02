package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Route;
import com.sustech.campus.entity.Station;
import com.sustech.campus.mapper.RouteMapper;
import com.sustech.campus.mapper.StationMapper;
import com.sustech.campus.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StationServiceImpl implements StationService {

    private final StationMapper stationMapper;

    private final RouteMapper routeMapper;

    @Autowired
    public StationServiceImpl(StationMapper stationMapper, RouteMapper routeMapper) {
        this.stationMapper = stationMapper;
        this.routeMapper = routeMapper;
    }

    @Override
    public List<Station> listAllStations() {
        return this.stationMapper.selectList(null);
    }

    @Override
    public boolean stationExists(Long stationId) {
        return this.getStationById(stationId) != null;
    }


    @Override
    public Long addStation(String stationName, double latitude, double longitude) {
        if (this.getStationByName(stationName) != null) {
            return null;
        }
        Station station = new Station();
        station.setName(stationName);
        station.setLatitude(latitude);
        station.setLongitude(longitude);
        this.stationMapper.insert(station);
        return station.getId();
    }

    @Override
    public boolean deleteStation(Long stationId) {
        if (!this.stationExists(stationId)) {
            return false;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("stationId", stationId);
        if (this.routeMapper.selectOne(wrapper) != null) {
            return false;
        }
        this.stationMapper.deleteById(stationId);
        return true;
    }

    @Override
    public Station getStationById(Long stationId) {
        return this.stationMapper.selectById(stationId);
    }

    @Override
    public Station getStationByName(String stationName) {
        QueryWrapper<Station> wrapper = new QueryWrapper<>();
        wrapper.eq("name", stationName);
        return this.stationMapper.selectOne(wrapper);
    }

    @Override
    public boolean editStation(long stationId, String newName, double newLatitude, double newLongitude) {
        Station station=this.getStationById(stationId);
        if(station==null)
            return false;
        station.setName(newName);
        station.setLatitude(newLatitude);
        station.setLongitude(newLongitude);
        this.stationMapper.updateById(station);
        return false;
    }

    @Override
    public boolean changeStationName(Long stationId, String newName) {
        Station station = this.getStationById(stationId);
        if (station == null) {
            return false;
        }
        station.setName(newName);
        this.stationMapper.updateById(station);
        return true;
    }

    @Override
    public boolean changeStationLocation(Long stationId, double newLatitude, double newLongitude) {
        Station station = this.getStationById(stationId);
        if (station == null) {
            return false;
        }
        station.setLatitude(newLatitude);
        station.setLongitude(newLongitude);
        this.stationMapper.updateById(station);
        return true;
    }

    @Override
    public List<Long> listAllBusIds(Long stationId) {
        if (!this.stationExists(stationId)) {
            return null;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("stationId", stationId);
        return this.routeMapper.selectList(wrapper).stream().map(Route::getBusId).distinct().toList();
    }
}
