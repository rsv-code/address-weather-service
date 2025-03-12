/**
 *     Copyright 2025 Austin Lehman (cup_of_code@fastmail.com)
 *
 *     This file is part of address-weather-service.
 *
 *     address-weather-service is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published
 *     by the Free Software Foundation, either version 3 of the License,
 *     or (at your option) any later version.
 *
 *     address-weather-service is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *     or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *     for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with address-weather-service. If not, see <https://www.gnu.org/licenses/>.
 */

package com.lehman.address_weather_service;

/**
 * Public class that contains decimal
 * Latitude and Longitude coordinate values.
 */
public class Coordinates {
    protected double latitude = 0.0;
    protected double longitude = 0.0;

    /**
     * Default constructor.
     */
    public Coordinates() { }

    /**
     * Sets the coordinate pair values.
     * @param latitude is a double with the latitude.
     * @param longitude is a double with the longitude.
     */
    public void set(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Gets the latitude value.
     * @return A double with the latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude value.
     * @param latitude is a double with the latitude value.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude value.
     * @return A double with the longitude.
     */
    public double getLongitude() {
        return longitude;
    }

     /**
     * Sets the longitude value.
     * @param longitude is a double with the longitude value.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Creates a String representation of the coordinate pair.
     * @return A String with the coordinates.
     */
    @Override
    public String toString() {
        return "(" + this.latitude + ", " + this.longitude + ")";
    }
}
