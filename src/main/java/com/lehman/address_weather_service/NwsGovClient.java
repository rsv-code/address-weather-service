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

import java.text.DecimalFormat;

/**
 * National Weather Service web client handles forecast requests.
 */
public class NwsGovClient {
    protected String url;

    private final RestClient client = RestClient.create();

    /**
     * Default constructor initializes the client with the provided
     * service URL.
     * @param url is a String with the NWS service URL.
     */
    public NwsGovClient(String url) {
        this.url = url;
    }

    /**
     * Gets the forecast for the provided set of coordinates from
     * the National Weather Service.
     * @param coordinates is a Coordinates object with the place to get
     * the forecast for.
     * @return A String with the forcast JSON result.
     * @throws JsonProcessingException
     */
    public String getForecast(Coordinates coordinates) throws JsonProcessingException {
        String ret = "{}";

        DecimalFormat df = new DecimalFormat("###.###");
        // Make the first request to get the general forecast information.
        String json = this.client.get()
            .uri(this.url, df.format(coordinates.getLatitude()), df.format(coordinates.getLongitude()))
            .retrieve()
            .body(String.class);

        // Parse the JSON result.
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(json);

        // Look for the forecast URL and then make the
        // request to get the actual forecast.
        JsonNode properties = root.get("properties");
        if (properties != null && properties.has("forecast")) {
            String forecastUrl = properties.get("forecast").asText();
            ret = this.getForcastUrl(forecastUrl);
        }

        return ret;
    }

    /**
     * Function used to make the actual request to get the
     * forecast with the provided URL.
     * @param forecastUrl is a String with the forecast URL.
     * @return A String with the forecast results JSON string.
     */
    private String getForcastUrl(String forecastUrl) {
        String json = this.client.get()
            .uri(forecastUrl)
            .retrieve()
            .body(String.class);
        return json;
    }
}
