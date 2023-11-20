package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Route;
import com.sustech.campus.entity.Station;
import com.sustech.campus.mapper.RouteMapper;
import com.sustech.campus.mapper.StationMapper;
import com.sustech.campus.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public boolean stationExists(String name) {
        return this.stationMapper.selectById(name) != null;
    }

    @Override
    public boolean addStation(String name, double latitude, double longitude) {
        if (this.stationExists(name)) {
            return false;
        }
        Station station = new Station();
        station.setName(name);
        station.setLatitude(latitude);
        station.setLongitude(longitude);
        this.stationMapper.insert(station);
        return true;
    }

    @Override
    public boolean deleteStation(String name) {
        if (!this.stationExists(name)) {
            return false;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("station", name);
        if (this.routeMapper.selectOne(wrapper) != null) {
            return false;
        }
        this.stationMapper.deleteById(name);
        return true;
    }

    @Override
    public Station getStation(String name) {
        return this.stationMapper.selectById(name);
    }

    @Override
    public boolean changeStationLocation(String name, double latitude, double longitude) {
        if (!this.stationExists(name)) {
            return false;
        }
        Station station = this.stationMapper.selectById(name);
        station.setLatitude(latitude);
        station.setLongitude(longitude);
        this.stationMapper.updateById(station);
        return true;
    }

    @Override
    public List<Bus> listAllBusLines(String name) {
        if (!this.stationExists(name)) {
            return null;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("station", name);
        return this.routeMapper.selectList(wrapper).stream().map(Route::getBus).distinct()
                .map(e -> {
                    Bus bus = new Bus();
                    bus.setName(e);
                    return bus;
                }).toList();
    }
}
