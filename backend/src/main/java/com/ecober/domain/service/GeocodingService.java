package com.ecober.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate;

    @Autowired
    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double[] getLatAndLong(String location) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=YOUR_API_KEY";
        String response = restTemplate.getForObject(url, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            var node = mapper.readTree(response);
            var results = node.path("results");

            if (results.isArray() && results.size() > 0) {
                var loc = results.get(0).path("geometry").path("location");
                return new double[]{
                        loc.path("lat").asDouble(),
                        loc.path("lng").asDouble()
                };
            } else {
                throw new RuntimeException("No geocoding result for: " + location);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error parsing geocoding response", e);
        }
    }
}
