package com.sustech.campus.service;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Station;

import java.util.List;

public interface BusService {
    /**
     * @param busName
     * @return null if failed to add such line, otherwise the id of the bus line
     */
    Long addBusLine(String busName);

    boolean addBusLine(String busLineName, List<Long> stations);

    /**
     * @param busId
     * @return whether deletion succeed
     */
    boolean deleteBusLine(Long busId);

    /**
     * @return all bus lines
     */
    List<Bus> listAllBusLines();

    /**
     * @param busId
     * @return whether the bus line with the given id exists
     */
    boolean busLineExists(Long busId);

    /**
     * @param busName
     * @return whether the bus line with the given name exists
     */
    boolean busLineExists(String busName);

    /**
     * @param busId
     * @return null if the bus line with the given id does not exist, otherwise, a List of the ids of stations
     * on the bus line, arranged in the order of stopping
     */
    List<Long> getStationIds(Long busId);

    /**
     * @param busId
     * @return null if the bus line with the given id does not exist, otherwise, a List of the stations
     * on the bus line, arranged in the order of stopping
     */
    List<Station> getStations(Long busId);

    /**
     * @param busId
     * @param stationIds ids of the stations on the line, arranged in the order of stopping
     * @return false if failed to change stations, otherwise true
     */
    boolean changeBusStations(Long busId, List<Long> stationIds);

    boolean edidtBuslineName(Long busId, String newName);
}
