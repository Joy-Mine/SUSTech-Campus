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
    public Long addBusLine(String busName) {
        if (this.busLineExists(busName)) {
            return null;
        }
        Bus bus = new Bus();
        bus.setName(busName);
        this.busMapper.insert(bus);
        return bus.getId();
    }

    @Override
    public boolean addBusLine(String busLineName, List<Long> stations) {
        if (this.busLineExists(busLineName)) {
            return false;
        }
        Bus bus = new Bus();
        bus.setName(busLineName);
        this.busMapper.insert(bus);
        for(int i=1;i<=stations.size();++i)
            routeMapper.insert(new Route(bus.getId(),stations.get(i-1),i));
        return true;
    }

    @Override
    public boolean deleteBusLine(Long busId) {
        if (!this.busLineExists(busId)) {
            return false;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("busId", busId);
        this.routeMapper.delete(wrapper);
        this.busMapper.deleteById(busId);
        return true;
    }

    @Override
    public List<Bus> listAllBusLines() {
        return this.busMapper.selectList(null);
    }

    @Override
    public boolean busLineExists(Long busId) {
        return this.busMapper.selectById(busId) != null;
    }

    @Override
    public boolean busLineExists(String busName) {
        QueryWrapper<Bus> wrapper = new QueryWrapper<>();
        wrapper.eq("name", busName);
        return this.busMapper.selectOne(wrapper) != null;
    }

    @Override
    public List<Long> getStationIds(Long busId) {
        if (!this.busLineExists(busId)) {
            return null;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("busId", busId);
        return this.routeMapper.selectList(wrapper).stream().sorted(Comparator.comparing(Route::getStopOrder))
                .map(Route::getStationId).toList();
    }

    @Override
    public List<Station> getStations(Long busId) {
        List<Long> stationIds = this.getStationIds(busId);
        if (stationIds == null) {
            return null;
        }
        List<Station> stations = new ArrayList<>();
        for (Long stationId : stationIds) {
            stations.add(this.stationMapper.selectById(stationId));
        }
        return stations;
    }

    @Override
    public boolean changeBusStations(Long busId, List<Long> stationIds) {
        if (!this.busLineExists(busId)) {
            return false;
        }
        for (Long stationId : stationIds) {
            if (this.stationMapper.selectById(stationId) == null) {
                return false;
            }
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("busId", busId);
        this.routeMapper.delete(wrapper);
        for (int i = 0; i < stationIds.size(); ++i) {
            Route route = new Route();
            route.setBusId(busId);
            route.setStationId(stationIds.get(i));
            route.setStopOrder(i);
            this.routeMapper.insert(route);
        }
        return true;
    }

    @Override
    public boolean edidtBuslineName(Long busId, String newName) {
        Bus bus=busMapper.selectById(busId);
        if (bus==null)
            return false;
        bus.setName(newName);
        this.busMapper.updateById(bus);
        return true;
    }
}
