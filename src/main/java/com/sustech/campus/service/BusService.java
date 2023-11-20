package com.sustech.campus.service;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Station;

import java.util.List;

public interface BusService {
    /**
     * @param name         name of the bus line
     * @param stationNames names of the stations on the line, arranged in the order of stopping
     * @return false if failed to add such line, otherwise true
     */
    boolean addBusLine(String name, List<String> stationNames);

    /**
     * @param name name of the bus line
     * @return whether deletion succeed
     */
    boolean deleteBusLine(String name);

    /**
     * @return all bus lines
     */
    List<Bus> listAllBusLines();

    /**
     * @param name name of the bus line
     * @return whether the bus line with the given name exists
     */
    boolean busLineExists(String name);

    /**
     * @param name name of the bus line
     * @return null if the bus line with the given name does not exist, otherwise, a List of the name of stations
     * on the bus line, arranged in the order of stopping
     */
    List<String> getStationNames(String name);

    /**
     * @param name name of the bus line
     * @return null if the bus line with the given name does not exist, otherwise, a List of the stations
     * on the bus line, arranged in the order of stopping
     */
    List<Station> getStations(String name);

    /**
     * @param name         name of the bus line
     * @param stationNames names of the stations on the line, arranged in the order of stopping
     * @return false if failed to change stations, otherwise true
     */
    boolean setStations(String name, List<String> stationNames);

}
