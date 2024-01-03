package com.sustech.campus.service;

import com.sustech.campus.entity.Station;

import java.util.List;

public interface StationService {
    /**
     * @return names of all stations
     */
    List<Station> listAllStations();

    /**
     * @param stationId
     * @return whether the station of the given id exists
     */
    boolean stationExists(Long stationId);

    /**
     * @param stationName
     * @param latitude
     * @param longitude
     * @return null if failed to add the station, otherwise the id of the station
     */
    Long addStation(String stationName, double latitude, double longitude);

    /**
     * @param stationId
     * @return false if failed to delete the station, otherwise true
     */
    boolean deleteStation(Long stationId);

    /**
     * @param stationId
     * @return null if such station does not exist, otherwise, a Station instance
     */
    Station getStationById(Long stationId);

    /**
     * @param stationName
     * @return null if such station does not exist, otherwise, a Station instance
     */
    Station getStationByName(String stationName);

    boolean editStation(long stationId, String newName, double newLatitude, double newLongitude);

    /**
     * @param stationId
     * @param newName
     * @return false if failed to change the name, otherwise true
     */
    boolean changeStationName(Long stationId, String newName);

    /**
     * @param stationId
     * @param newLatitude
     * @param newLongitude
     * @return false if failed to change the location, otherwise true
     */
    boolean changeStationLocation(Long stationId, double newLatitude, double newLongitude);

    /**
     * @param stationId
     * @return ids of bus that stop at the given station
     */
    List<Long> listAllBusIds(Long stationId);
}
