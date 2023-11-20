package com.sustech.campus.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Route;
import com.sustech.campus.entity.Station;
import com.sustech.campus.mapper.BusMapper;
import com.sustech.campus.mapper.RouteMapper;
import com.sustech.campus.mapper.StationMapper;
import com.sustech.campus.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BusServiceImpl implements BusService {
    private BusMapper busMapper;
    private RouteMapper routeMapper;

    private StationMapper stationMapper;

    @Autowired
    public BusServiceImpl(BusMapper busMapper, RouteMapper routeMapper, StationMapper stationMapper) {
        this.busMapper = busMapper;
        this.routeMapper = routeMapper;
        this.stationMapper = stationMapper;
    }


    @Override
    public boolean addBusLine(String name, List<String> stationNames) {
        if (this.busLineExists(name)) {
            return false;
        }
        for (String stationName : stationNames) {
            if (this.stationMapper.selectById(stationName) == null) {
                return false;
            }
        }
        Bus busLine = new Bus();
        busLine.setName(name);
        this.busMapper.insert(busLine);
        for (int i = 0; i < stationNames.size(); ++i) {
            Route route = new Route();
            route.setBus(name);
            route.setStation(stationNames.get(i));
            route.setStopOrder(i);
            this.routeMapper.insert(route);
        }
        return true;
    }

    @Override
    public boolean deleteBusLine(String name) {
        if (!this.busLineExists(name)) {
            return false;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("bus", name);
        this.routeMapper.delete(wrapper);
        this.busMapper.deleteById(name);
        return true;
    }

    @Override
    public List<Bus> listAllBusLines() {
        return this.busMapper.selectList(null);
    }

    @Override
    public boolean busLineExists(String name) {
        return this.busMapper.selectById(name) != null;
    }

    @Override
    public List<String> getStationNames(String name) {
        if (!this.busLineExists(name)) {
            return null;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("bus", name);
        return this.routeMapper.selectList(wrapper).stream().sorted(Comparator.comparing(Route::getStopOrder))
                .map(Route::getStation).toList();
    }

    @Override
    public List<Station> getStations(String name) {
        List<String> stationNames = this.getStationNames(name);
        if (stationNames == null) {
            return null;
        }
        List<Station> stations = new ArrayList<>();
        for (String stationName : stationNames) {
            stations.add(this.stationMapper.selectById(stationName));
        }
        return stations;
    }

    @Override
    public boolean setStations(String name, List<String> stationNames) {
        if (!this.busLineExists(name)) {
            return false;
        }
        for (String stationName : stationNames) {
            if (this.stationMapper.selectById(stationName) == null) {
                return false;
            }
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("bus", name);
        this.routeMapper.delete(wrapper);
        for (int i = 0; i < stationNames.size(); ++i) {
            Route route = new Route();
            route.setBus(name);
            route.setStation(stationNames.get(i));
            route.setStopOrder(i);
            this.routeMapper.insert(route);
        }
        return true;
    }
}
