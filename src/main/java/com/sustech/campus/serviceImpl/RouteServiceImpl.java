package com.sustech.campus.serviceImpl;

import com.sustech.campus.entity.*;
import com.sustech.campus.mapper.RouteMapper;
import com.sustech.campus.service.BuildingService;
import com.sustech.campus.service.BusService;
import com.sustech.campus.service.RouteService;
import com.sustech.campus.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteServiceImpl implements RouteService {

    private final BuildingService buildingService;
    private final StationService stationService;
    private final RouteMapper routeMapper;
    private final BusService busService;

    @Autowired
    public RouteServiceImpl(BuildingService buildingService, StationService stationService, RouteMapper routeMapper, BusService busService) {
        this.buildingService = buildingService;
        this.stationService = stationService;
        this.routeMapper = routeMapper;
        this.busService = busService;
    }

    @Override
    public List<RouteInfo> getAllRoutes() {
        List<RouteInfo> allRoutes=new ArrayList<>();
        List<Bus> busLines=busService.listAllBusLines();
        for(Bus busLine : busLines){
            allRoutes.add(new RouteInfo(busLine.getId(),busLine.getName(),busService.getStations(busLine.getId())));
        }
        return allRoutes;
    }

    @Override
    public List<Station> planRoute(long buildingId1, long buildingId2) {
        Building building1 = buildingService.getBuildingById(buildingId1);
        Building building2 = buildingService.getBuildingById(buildingId2);

        //findNearestStation()
        Station nearestStation1=null, nearestStation2 = null;
        double nearestDistance1=Double.MAX_VALUE, nearestDistance2 = Double.MAX_VALUE;
        List<Station> stations = stationService.listAllStations();
        for (Station station : stations) {
            double distance = this.calculateDistance(building1.getLatitude(), building1.getLongitude(), station.getLatitude(), station.getLongitude());
            if (distance < nearestDistance1) {
                nearestDistance1 = distance;
                nearestStation1 = station;
            }
        }
        for (Station station : stations) {
            double distance = this.calculateDistance(building2.getLatitude(), building2.getLongitude(), station.getLatitude(), station.getLongitude());
            if (distance < nearestDistance2) {
                nearestDistance2 = distance;
                nearestStation2 = station;
            }
        }

        List<Route> routes=routeMapper.selectList(null);
        Map<Long, StationNode> graph = buildGraph(stations, routes);
        System.out.println(graph);
        return dijkstra(graph, nearestStation1.getId(), nearestStation2.getId());
    }
    private Map<Long, StationNode> buildGraph(List<Station> stations, List<Route> routes) {
        Map<Long, StationNode> graph = new HashMap<>();
        for (Station station : stations)
            graph.put(station.getId(), new StationNode(station.getId(), station.getLatitude(), station.getLongitude()));
//        for (Station station1 : stations) {
//            for (Station station2 : stations) {
//                if (!station1.equals(station2)) {
//                    double distance = calculateDistance(station1.getLatitude(), station1.getLongitude(),
//                            station2.getLatitude(), station2.getLongitude());
//                    graph.get(station1.getId()).addEdge(new StationEdge(graph.get(station2.getId()), distance));
//                }
//            }
//        }
        Map<Long, PriorityQueue<StationOrder>> stationsOnABusline=new HashMap<>();
        for (Route route : routes){
            if(stationsOnABusline.containsKey(route.getBusId()))
                stationsOnABusline.get(route.getBusId()).add(new StationOrder(route.getStationId(),route.getStopOrder()));
            else {
                stationsOnABusline.put(route.getBusId(), new PriorityQueue<>(Comparator.comparing(StationOrder::getStopOrder)));
                stationsOnABusline.get(route.getBusId()).add(new StationOrder(route.getStationId(),route.getStopOrder()));
            }
        }
        //遍历stationsOnABusline即可
        for (Long key : stationsOnABusline.keySet()){
            PriorityQueue<StationOrder> aBusline=stationsOnABusline.get(key);
            Long preId=aBusline.poll().getStationId();
            StationNode preSta=graph.get(preId);
            Long curId=null;
            StationNode curSta=null;
            while (!aBusline.isEmpty()){
                curId=aBusline.poll().getStationId();
                curSta=graph.get(curId);
                double distance=calculateDistance(preSta.getLatitude(),preSta.getLongitude(),curSta.getLatitude(),curSta.getLongitude());
                preSta.addEdge(new StationEdge(curSta, distance));
                curSta.addEdge(new StationEdge(preSta, distance));
                preSta=curSta;
                preId=curId;
            }
        }
        return graph;
    }
    static class StationOrder{
        private Long stationId;
        private int stopOrder;
        public StationOrder(Long stationId, int stopOrder) {
            this.stationId = stationId;
            this.stopOrder = stopOrder;
        }
        public Long getStationId() {
            return stationId;
        }
        public int getStopOrder() {
            return stopOrder;
        }
    }
    private List<Station> dijkstra(Map<Long, StationNode> graph, long startId, long endId) {
        PriorityQueue<StationNode> queue = new PriorityQueue<>(Comparator.comparing(StationNode::getDistance));
        StationNode startNode = graph.get(startId);
        startNode.setDistance(0);
        queue.add(startNode);
        while (!queue.isEmpty()) {
            StationNode current = queue.poll();
            if (current.getId() == endId)
                break;
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
    private List<Station> buildPath(Map<Long, StationNode> graph, long endId) {
        LinkedList<Station> path = new LinkedList<>();
        StationNode target = graph.get(endId);
        path.addFirst(stationService.getStationById(target.getId()));
        while (target != null && target.getPrevious() != null) {
            target = target.getPrevious();
            path.addFirst(stationService.getStationById(target.getId()));
        }
        return path;
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
        public double getLatitude() {
            return latitude;
        }
        public double getLongitude() {
            return longitude;
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
}