package com.ecober.adapter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.service.Co2AnalyticsService;
import com.ecober.domain.service.DriverMatchingService;
import com.ecober.domain.service.RiderService;
import com.ecober.domain.service.IntentService;

import com.ecober.domain.service.RouteOptimizingService;
import java.util.Map;
import org.springframework.http.HttpStatus;


import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    RiderService riderService;

    @Autowired
    IntentService intentService;

    @Autowired
    DriverMatchingService fetchNearestRiderService;

    @Autowired
    Co2AnalyticsService co2AnalyticsService;

    @Autowired
    RouteOptimizingService routeOptimizingService;

    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@NotNull @RequestBody RiderDTO riderDTO)
    {
        DriverDTO matchedDriver=fetchNearestRiderService.fetchNearestDriver(riderDTO.getRiderId(),riderDTO.getRiderPickupLocation(),riderDTO.getRiderDropOffLocation(),riderDTO.getPickupLatitude(),riderDTO.getPickupLongitude(),riderDTO.getDropoffLatitude(),riderDTO.getDropoffLongitude(),riderDTO.getPreferredVehicleType(),riderDTO.isWillingToPool());
        return ResponseEntity.ok(matchedDriver);
    }

        @PostMapping("/chatbotIntent")
        public ResponseEntity<String> chatIntent(@RequestBody Map<String, String> payload) {
            String userMessage = payload.get("message");
            String intentJson = intentService.getIntent(userMessage);
            return ResponseEntity.ok(intentJson);
        }

    @GetMapping("/distanceDuration/{riderId}")
    public ResponseEntity<DistanceDurationDTO> requestDistanceDuration(@PathVariable String riderId) {
        double pickupLat = 12.9352;
        double pickupLong = 77.6245;
        double dropoffLat = 12.9716;
        double dropoffLong = 77.5946;

        DistanceDurationDTO dto = routeOptimizingService.getDistanceAndETA(pickupLat, pickupLong, dropoffLat, dropoffLong);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/chatbot")
public ResponseEntity<String> chat(@RequestBody Map<String, String> payload) {
    String userMessage = payload.get("message");
    String intentJson = intentService.getIntent(userMessage);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
        Map<String, String> intentMap = objectMapper.readValue(intentJson, new TypeReference<>() {});
        String intent = intentMap.get("intent");

        return switch (intent) {
            case "get_last_ride_emission" -> ResponseEntity.ok("Your last ride emitted 1.4 kg of CO₂ 🌱");
            case "get_weekly_emission" -> ResponseEntity.ok("You emitted 7.6 kg of CO₂ this week 🌍");
            case "get_monthly_emission" -> ResponseEntity.ok("You emitted 32.1 kg of CO₂ this month 📅");
            case "get_all_trips" -> ResponseEntity.ok("You have taken 12 trips so far 🚗");
            default -> ResponseEntity.ok("Sorry, I couldn't understand that. Try asking about emissions or trips.");
        };
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error parsing intent: " + e.getMessage());
    }
}

    @GetMapping("/carbonEmission/{riderId}")
    public ResponseEntity<CarbonDTO> requestCarbonEmission(@PathVariable String riderId)
    {
        CarbonDTO carbonDTO=co2AnalyticsService.getRiderCarbonEmission(riderId);
        return ResponseEntity.ok(carbonDTO);
    }

    @GetMapping("/riderService/getTrips/{riderID}")
    public ResponseEntity<List<TripDTO>> requestAllTrips(@PathVariable String riderId)
    {
        List<TripDTO>trips=riderService.fetchAllTrips(riderId);
        return ResponseEntity.ok(trips);
    }
}
