# address-weather-service

## Purpose

The Address Weather Service application is a web service that handles 
forecast requests. It takes the provided address, calls the U.S. Census 
Bureau API to geocode the addresss. It then takes that geocoded address (as 
a coordiate pair) and makes the forecast request to the National Weather 
Service API. The result is then cached by zipcode. It returns the forecast 
result along with the cached flag in 
JSON format. If subsequent requests are made to the same zipcode, the cached 
result will be returned without making any of the API calls.


## Dependencies

This application requires Java version 23 along with Maven installed locally.


## Configuration

The application is configured using the `application.properties` file 
located in the `src/main/resources` directory.

You can configure the NWS and Census Bureau web service URLs, and you can 
set the following controlling the caching of the results by zipcode.

```
# Cache duration minutes.
weatherservice.cacheExpiresMinutes=30

# Max number of entries allowed in the cache.
weatherservice.cacheMaxNumberEntries=1000
```

## Starting the Application

You can start the application from the command line in the root directory by 
running the following command.

```
mvn compile exec:java
```

Once the service is running you can reach it on `localhost` port `8080`.

## Making Forecast Requests

Included in the project root directory is a Postman collection that can be 
used to execute requests against the service. Import the 
address-weather-service.postman_collection.json file and run the `forecast` 
GET request after starting the web service as described in the `Starting the 
Application` section.


You can also just put the following URL in your browser to view the JSON 
response.

```
http://localhost:8080/forecast?street=1261%20Pleasant%20Grove%20Blvd&city=Roseville&state=CA&zipcode=95747
```

## Running the Unit Tests

From the command line in the root directory run the following commad.

```
mvn clean test
```

## Todo

I had limited time to work on this application, so given more time here's 
what I would add.

* Marshall JSON response data into objects and create specific result 
  objects with select pieces of data.
* More error checking and provide specific error messages.
* Create a web UI for making the request and displaying the response.

## License

Copyright 2025 Austin Lehman (cup_of_code@fastmail.com)

This file is part of address-weather-service.
 address-weather-service is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by 
the Free Software Foundation, either version 3 of the License,or (at your option) any later version.

address-weather-service is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with address-weather-service. If not, see <https://www.gnu.org/licenses/>.