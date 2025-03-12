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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * CensusGovGeocodeClient test class.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class CensusGovGeocodeClientTest {
    @Value("classpath:CensusGovGeocodeClientResult1.json")
    private Resource geocodeResult;

    @Value( "${weatherservice.geocodeUrl}" )
    private String weatherServiceUrl;

    private CensusGovGeocodeClient client;

    /**
     * Create the client for the tests to use.
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        this.client = new CensusGovGeocodeClient(this.weatherServiceUrl);
    }

    /**
     * Runs the entire geocode process with HTTP request.
     * @throws JsonProcessingException
     */
    @Test
    public void getGeocodeAddress() throws JsonProcessingException {
        // Geocode an address
        Coordinates coordinates = this.client.geocode("1261 Pleasant Grove Blvd", "Roseville", "CA", "95747");

        // Verify returned coordinates
        assertThat(coordinates.getLongitude()).isEqualTo(-121.316399912491);
        assertThat(coordinates.getLatitude()).isEqualTo(38.771887717945);
    }

    /**
     * Tests the getCoordinatesFromCensusJson function that extracts
     * the coordinates from the result JSON.
     * @throws IOException
     */
    @Test
    public void getCoordinatesFromCensusJsonTest() throws IOException {
        // Geocode the result from resource file.
        Coordinates coordinates = this.client.getCoordinatesFromCensusJson(
                this.geocodeResult.getContentAsString(Charset.defaultCharset())
        );

        // Verify returned coordinates
        assertThat(coordinates.getLongitude()).isEqualTo(-121.316399912491);
        assertThat(coordinates.getLatitude()).isEqualTo(38.771887717945);
    }
}
