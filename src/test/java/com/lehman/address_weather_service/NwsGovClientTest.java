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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * NwsGovClient test class.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
public class NwsGovClientTest {
    @Value( "${weatherservice.nwsUrl}" )
	protected String nwsServiceUrl;

    private NwsGovClient client;

    /**
     * Create the client for the tests to use.
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        this.client = new NwsGovClient(this.nwsServiceUrl);
    }

    /**
     * Runs the entire get forecast function with HTTP requests.
     * @throws JsonProcessingException
     */
    @Test
    public void getForecastTest() throws IOException {
        // Create the coordinate pair.
        Coordinates coordinates = new Coordinates();
        coordinates.set(38.771887717945, -121.316399912491);

        // Make request to NWS.
        String jsonResult = this.client.getForecast(coordinates);

        // Parse the result JSON.
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonResult);


        // Verify the result JSON.
        assertThat(root.get("type").asText()).isEqualTo("Feature");
        assertThat(root.get("properties")).isNotNull();
    }
}
