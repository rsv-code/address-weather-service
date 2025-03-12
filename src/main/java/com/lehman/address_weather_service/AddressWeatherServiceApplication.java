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
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * AddressWeatherServiceApplication is the spring application
 * and controller that implements the address weather service.
 */
@SpringBootApplication
@Controller
@ResponseBody
@Component
@PropertySource("classpath:application.properties")
public class AddressWeatherServiceApplication {
	private static Logger logger = LogManager.getLogger(AddressWeatherServiceApplication.class);

	@Value( "${weatherservice.geocodeUrl}" )
    protected String weatherServiceUrl;

	@Value( "${weatherservice.nwsUrl}" )
	protected String nwsServiceUrl;

	protected int cacheExpiresMinutes;
	protected int cacheMaxNumberEntries;

	protected Cache<String, String> cache;

	/**
	 * The web service main entry point.
	 * @param args is an Array of Strings.
	 */
	public static void main(String[] args) {
		// Startup the spring weather service application.
		SpringApplication.run(AddressWeatherServiceApplication.class, args);
	}

	/**
	 * Default constructor sets up application objects.
	 * @param cacheExpiresMinutes is an int with the number of
	 * minutes to keep the cached results.
	 * @param cacheMaxNumberEntries is an int with the max
	 * number of entries to keep in the cache.
	 */
	public AddressWeatherServiceApplication(
			@Value("${weatherservice.cacheExpiresMinutes}") int cacheExpiresMinutes,
			@Value("${weatherservice.cacheMaxNumberEntries}") int cacheMaxNumberEntries
	) {
		this.cacheExpiresMinutes = cacheExpiresMinutes;
		this.cacheMaxNumberEntries = cacheMaxNumberEntries;

		logger.info("Initializing the cache. (expires=" + this.cacheExpiresMinutes + " maxEntries=" + this.cacheMaxNumberEntries + ")");
		// Create the cache.
		this.cache = Caffeine.newBuilder()
            .expireAfterWrite(this.cacheExpiresMinutes, TimeUnit.MINUTES)
            .maximumSize(this.cacheMaxNumberEntries)
            .build();
	}

	/**
	 * The forecast function/endpoint takes the address as input and
	 * returns a JSON formatted String with the results.
	 * @param street is a String with the street address line 1.
	 * @param city is a String with the city name.
	 * @param state is a String with the 2 letter state abbreviation.
	 * @param zipcode is a String with the zipcode.
	 * @return A String with the forecast in JSON format.
	 */
	@GetMapping("/forecast")
    public String forecast(
		HttpServletResponse response,
		@RequestParam(value = "street") String street,
		@RequestParam(value = "city") String city,
		@RequestParam(value = "state") String state,
		@RequestParam(value = "zipcode") String zipcode
	) {
		String address = street + ", " + city + ", " + state + " " + zipcode;
		response.setContentType("application/json");

		logger.debug("Attempting to get zipcode " + zipcode + " from the cache.");
		String forecastStr = this.cache.getIfPresent(zipcode);
		if (forecastStr != null) {
			// Cached forecast found, return it from cache.
			logger.debug("Cache hit for zipcode " + zipcode + ".");
			return this.formatResult(forecastStr, true);
		} else {
			// Cache miss, attempt to get the geocoded
			// coordinates from the Census Bureau.
			logger.debug("Cache miss for zipcode " + zipcode + ".");
			CensusGovGeocodeClient geoCodeService = new CensusGovGeocodeClient(this.weatherServiceUrl);
			try {
				Coordinates coordinates = geoCodeService.geocode(street, city, state, zipcode);
				if (coordinates != null) {
					logger.debug("Found coordinates " + coordinates.toString() + " for address: '" + address + "'");

					NwsGovClient nwsGovClient = new NwsGovClient(this.nwsServiceUrl);
					forecastStr = nwsGovClient.getForecast(coordinates);

					// Add to cache for zipcode.
					logger.debug("Adding " + zipcode + " to the cache.");
					this.cache.put(zipcode, forecastStr);

					return this.formatResult(forecastStr, false);
				} else {
					logger.warn("No coordinates found for address: '" + address + "'");
				}
			} catch (JsonProcessingException e) {
				logger.error("JsonProcessingException: " + e.getMessage());
				throw new RuntimeException(e);
			}
		}

        return "{ \"success\": false, \"message\": \"Forecast not found for the provided address.\" }";
    }

	/**
	 * Formats the result JSON String with the forecast result JSON
	 * and the provided cached flag.
	 * @param forecastStr is a JSON String with the forecast result.
	 * @param cached is a boolean with true for cache hit and false for not.
	 * @return A JSON encoded String with the result.
	 */
	protected String formatResult(String forecastStr, boolean cached) {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"forecast\": ");
		sb.append(forecastStr);
		sb.append(", \"cached\": ");
		sb.append(cached);
		sb.append(" }");
		return sb.toString();
	}
}
