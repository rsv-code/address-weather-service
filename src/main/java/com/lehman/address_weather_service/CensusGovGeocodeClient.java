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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;


/**
 * Census Bureau web client handles geocoding requests.
 */
public class CensusGovGeocodeClient {
    private RestClient client = RestClient.create();
    protected String url;

    /**
     * Default constructor takes a URL to use.
     * @param url is a String with the URL to use.
     */
    public CensusGovGeocodeClient(String url) {
        this.url = url;
    }

    /**
     * Geocodes the provided address and returns the corresponding
     * latitude and longitude coordinate pair.
     * @param street is a String with the first line of the address.
     * @param city is a String with the city.
     * @param state is a String with the two character state code.
     * @param zipcode is a String with the zipcode.
     * @return A Coordinate object with the geocoded point.
     * @throws JsonProcessingException
     */
    public Coordinates geocode(String street, String city, String state, String zipcode) throws JsonProcessingException {
        String json = this.client.get()
            .uri(this.url, street, city, state, zipcode)
            .retrieve()
            .body(String.class);

        return this.getCoordinatesFromCensusJson(json);
    }

    /**
     * Parses the provided Census JSON String and returns a Coordinates
     * object or null if not found.
     * @param json is a String with the result of the Census Bureau response.
     * @return A Coordinates object or null if not found.
     * @throws JsonProcessingException
     */
    public Coordinates getCoordinatesFromCensusJson(String json) throws JsonProcessingException {
        Coordinates coordinates = null;

        // Parse the result JSON
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(json);

        // Get the coordinate values and set them in the return object.
        JsonNode result = root.get("result");
        if (result != null && result.has("addressMatches")) {
            JsonNode matches = result.get("addressMatches");
            if(matches.size() > 0) {
                JsonNode match = matches.get(0);
                coordinates = new Coordinates();
                coordinates.setLongitude(match.get("coordinates").get("x").asDouble());
                coordinates.setLatitude(match.get("coordinates").get("y").asDouble());
                return coordinates;
            }
        }

        return coordinates;
    }
}
