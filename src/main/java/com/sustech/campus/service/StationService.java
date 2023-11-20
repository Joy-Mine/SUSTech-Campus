package com.sustech.campus.service;

import com.sustech.campus.entity.Bus;
import com.sustech.campus.entity.Station;

import java.util.List;

public interface StationService {
    /**
     * @return names of all stations
     */
    List<Station> listAllStations();

    /**
     * @param name name of the station
     * @return whether the station of the given name exists
     */
    boolean stationExists(String name);

    /**
     * @param name      name of the station
     * @param latitude
     * @param longitude
     * @return false if failed to add the station, otherwise true
     */
    boolean addStation(String name, double latitude, double longitude);

    /**
     * @param name name of the station
     * @return false if failed to delete the station, otherwise true
     */
    boolean deleteStation(String name);

    /**
     * @param name name of the station
     * @return null if such station does not exist, otherwise, a Station instance
     */
    Station getStation(String name);

    /**
     * @param name      name of the station
     * @param latitude  new latitude
     * @param longitude new longitude
     * @return false if failed to change the location, otherwise true
     */
    boolean changeStationLocation(String name, double latitude, double longitude);

    /**
     * @param name name of the station
     * @return bus lines that stop at the given station
     */
    List<Bus> listAllBusLines(String name);
}
