package com.ecober.util;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class FuelMappingUtil {

    private final RestTemplate restTemplate;

    public FuelMappingUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getFuelTypeByVin(String vin) {
        if (vin == null || vin.isBlank()) {
            throw new IllegalArgumentException("VIN must be provided to fetch fuel type.");
        }

        try {
            String url = UriComponentsBuilder
                    .fromUriString("https://vpic.nhtsa.dot.gov/api/vehicles/DecodeVinValues/{vin}")
                    .queryParam("format", "json")
                    .buildAndExpand(vin)
                    .toUriString();

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("Results");

            if (results != null && !results.isEmpty()) {
                String fuelType = (String) results.get(0).get("FuelTypePrimary");
                return fuelType != null ? fuelType : "Unknown";
            }
        } catch (Exception e) {
            System.err.println("Error fetching fuel type by VIN: " + e.getMessage());
        }
        return "Unknown";
    }
}
