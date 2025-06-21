package com.ecober.domain.service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService
{
    @Value("${gmap.api.key}")
    private String gmapKey;

    RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
}

public  double[] getLatAndLong(String location) {
    String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" +
            URLEncoder.encode(location, StandardCharsets.UTF_8) +
            "&key=" + gmapKey;

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    JSONObject json = new JSONObject(response.getBody());

    // Check for empty results array
    if (!json.has("results") || json.getJSONArray("results").isEmpty()) {
        throw new RuntimeException("No geocoding result found for: " + location);
    }

    JSONObject jsonLocation = json.getJSONArray("results")
            .getJSONObject(0)
            .getJSONObject("geometry")
            .getJSONObject("location");

    double lat = jsonLocation.getDouble("lat");
    double lng = jsonLocation.getDouble("lng");

    return new double[]{lat, lng};
}


    }
