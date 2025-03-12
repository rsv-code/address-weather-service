package com.lehman.address_weather_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Address Weather Service Application tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AddressWeatherServiceApplicationTests {
	@Autowired
	private AddressWeatherServiceApplication application;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	/**
	 * Tests that the application context is created.
	 */
	@Test
	void contextLoads() {
		assertThat(application).isNotNull();
	}

	/**
	 * Makes a request to the /forcast endpoint with a valid address
	 * and checks for a valid response.
	 * @throws Exception
	 */
	@Test
	void forecastShouldReturnResult() throws Exception {
		assertThat(this.restTemplate.getForObject(
		"http://localhost:" + port + "/forecast?street=1261 Pleasant Grove Blvd&city=Roseville&state=CA&zipcode=95747",
			String.class
		)).contains("\"periods\":");
	}
}
