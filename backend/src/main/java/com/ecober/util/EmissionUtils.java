package com.ecober.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EmissionUtils {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${climatiq.api.key}")
    private String API_KEY;

    private String API_URL="https://beta4.api.climatiq.io/estimate";

    public double Co2ActualEmission(double distanceKm, String vehicleType) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + API_KEY);
    headers.setContentType(MediaType.APPLICATION_JSON);

    String activityId = switch (vehicleType.toUpperCase()) {
        case "SUV"    -> "passenger_vehicle-vehicle_type_sport_utility_vehicle-fuel_source_na-distance_na-engine_size_na";
        case "SEDAN"  -> "passenger_vehicle-vehicle_type_car-fuel_source_na-distance_na-engine_size_na";
        case "EV"     -> "passenger_vehicle-vehicle_type_car-fuel_source_electricity-distance_na-engine_size_na";
        case "HYBRID" -> "passenger_vehicle-vehicle_type_car-fuel_source_hybrid-distance_na-engine_size_na";
        case "BIKE"   -> "passenger_vehicle-vehicle_type_motorcycle-fuel_source_na-distance_na-engine_size_na";
        default       -> "passenger_vehicle-vehicle_type_car-fuel_source_na-distance_na-engine_size_na";
    };

    Map<String, Object> body = new HashMap<>();
    body.put("emission_factor", Map.of(
        "activity_id", activityId,
        "data_version", "22.22"
    ));
    body.put("parameters", Map.of(
        "distance", distanceKm,
        "distance_unit", "km"
    ));

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
    Map<String, Object> result = response.getBody();

    if (result != null && result.containsKey("co2e")) {
        return ((Number) result.get("co2e")).doubleValue(); // Cast safely
    }

    throw new RuntimeException("Failed to fetch CO2 estimate");
}

    public double Co2EstimatedEmission(double distanceKm, String vehicleType) {
        double emissionFactor;

        switch (vehicleType.toUpperCase()) {
            case "EV"     -> emissionFactor = 0.18;
            case "HYBRID" -> emissionFactor = 0.104;
            case "SEDAN"  -> emissionFactor = 0.173;
            case "SUV"    -> emissionFactor = 0.231;
            case "BIKE"   -> emissionFactor = 0.0;
            default       -> emissionFactor = 0.21; 
        }

        return distanceKm * emissionFactor;
    }    
}
