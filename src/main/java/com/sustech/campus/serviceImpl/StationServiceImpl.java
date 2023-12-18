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

    @Override
    public Station findNearestStation(double latitude, double longitude) {
        List<Station> stations = this.listAllStations();
        Station nearestStation = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Station station : stations) {
            double distance = calculateDistance(latitude, longitude, station.getLatitude(), station.getLongitude());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestStation = station;
            }
        }
        return nearestStation;
    }

//    @Override
//    public List<Long> findRouteBetweenStations(long stationId1, long stationId2) {
//        Set<Long> routesStation1 = getRoutesForStation(stationId1);
//        Set<Long> routesStation2 = getRoutesForStation(stationId2);
//
//        for (Long routeId : routesStation1) {
//            if (routesStation2.contains(routeId)) {
//                // 如果两个站点共享同一公交线路
//                return getStationsForRoute(routeId, stationId1, stationId2);
//            }
//        }
//        // 复杂情况：需要找到一个中转站，
//        return new ArrayList<>();
//    }

    @Override
    public List<Long> findRouteBetweenStations(long startStationId, long endStationId) {
        List<Station> allStations=this.listAllStations();
        Map<Long, StationNode> graph = buildGraph(allStations);
        return dijkstra(graph, startStationId, endStationId);
    }
    private Map<Long, StationNode> buildGraph(List<Station> allStations) {
        Map<Long, StationNode> graph = new HashMap<>();
        for (Station station : allStations) {
            graph.put(station.getId(), new StationNode(station.getId(), station.getLatitude(), station.getLongitude()));
        }
        for (Station station1 : allStations) {
            for (Station station2 : allStations) {
                if (!station1.equals(station2)) {
                    double distance = calculateDistance(station1.getLatitude(), station1.getLongitude(),
                            station2.getLatitude(), station2.getLongitude());
                    graph.get(station1.getId()).addEdge(new StationEdge(graph.get(station2.getId()), distance));
                }
            }
        }
        return graph;
    }
    private List<Long> dijkstra(Map<Long, StationNode> graph, long startId, long endId) {
        PriorityQueue<StationNode> queue = new PriorityQueue<>(Comparator.comparing(StationNode::getDistance));
        StationNode startNode = graph.get(startId);
        startNode.setDistance(0);
        queue.add(startNode);
        while (!queue.isEmpty()) {
            StationNode current = queue.poll();
            if (current.getId() == endId) {
                break;
            }
            for (StationEdge edge : current.getEdges()) {
                StationNode neighbor = edge.getTarget();
                double newDist = current.getDistance() + edge.getWeight();
                if (newDist < neighbor.getDistance()) {
                    neighbor.setDistance(newDist);
                    neighbor.setPrevious(current);
                    if (!queue.contains(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }
        }
        return buildPath(graph, endId);
    }

    private List<Long> buildPath(Map<Long, StationNode> graph, long endId) {
        LinkedList<Long> path = new LinkedList<>();
        StationNode target = graph.get(endId);
        while (target != null && target.getPrevious() != null) {
            path.addFirst(target.getId());
            target = target.getPrevious();
        }
        return path;
    }

    // Inner classes for StationNode and StationEdge
    static class StationNode {
        private long id;
        private double latitude;
        private double longitude;
        private List<StationEdge> edges;
        private double distance = Double.MAX_VALUE;
        private StationNode previous = null;
        public StationNode(long id, double latitude, double longitude) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
            this.edges = new ArrayList<>();
        }
        public void addEdge(StationEdge edge) {
            edges.add(edge);
        }
        public long getId() {
            return id;
        }
        public double getDistance() {
            return distance;
        }
        public void setDistance(double distance) {
            this.distance = distance;
        }
        public StationNode getPrevious() {
            return previous;
        }
        public void setPrevious(StationNode previous) {
            this.previous = previous;
        }
        public List<StationEdge> getEdges() {
            return edges;
        }
    }
    static class StationEdge {
        private StationNode target;
        private double weight;
        public StationEdge(StationNode target, double weight) {
            this.target = target;
            this.weight = weight;
        }
        public StationNode getTarget() {
            return target;
        }
        public double getWeight() {
            return weight;
        }
    }

    //使用Haversine公式计算距离
    private static final double EARTH_RADIUS = 6371.0; // 地球半径（千米）
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
