package com.ecober.adapter.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.service.RideRequestService;
import com.ecober.domain.service.RouteOptimizingService;
import com.ecober.domain.service.TripService;
import com.ecober.util.AuthUtil;
import com.ecober.util.EmissionUtils;
import com.ecober.util.GeoUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ride")
@SecurityRequirement(name = "bearerAuth")
public class RideController {

    @Autowired
    private TripService tripService;

    @Autowired
    private RideRequestService rideRequestService;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    private EmissionUtils emissionUtils;

    @PostMapping("/requestRide")
public ResponseEntity<?> requestRide(@Valid @RequestBody RideRequestDTO rideDTO) {
    try {
        UUID userId = AuthUtil.getCurrentUserId();
        String role = AuthUtil.getCurrentUserRole();

        if (userId == null || !"RIDER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).body(Map.of("error", "Only authenticated riders can request rides."));
        }

        rideRequestService.processRideRequest(rideDTO, userId);
        return ResponseEntity.ok(Map.of("message", "Ride request broadcasted to nearby drivers."));

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid input: " + e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", "Error processing ride request: " + e.getMessage()));
    }
}

    
    @GetMapping("/distanceDuration/{tripId}")
    public ResponseEntity<?> requestDistanceDuration(@PathVariable UUID tripId) {
        try {
            UUID userId = AuthUtil.getCurrentUserId();
            Trip trip = tripService.getTripByIdForUser(tripId, userId);

            if (trip == null) {
                return ResponseEntity.status(403).body("Unauthorized or invalid trip.");
            }

            Route route = trip.getRoute();
            DistanceDurationDTO dto = routeOptimizingService.getDistanceAndETA(
                route.getSource(), route.getDestination());
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to calculate distance/ETA: " + e.getMessage());
        }
    }

    @GetMapping("/emissionEstimate")
public ResponseEntity<?> estimateEmission(
        @RequestParam double distanceKm,
        @RequestParam String vehicleType
) {
    try {
        double emissionKg = emissionUtils.Co2ActualEmission(distanceKm, vehicleType);
        return ResponseEntity.ok(Map.of(
            "vehicleType", vehicleType,
            "distanceKm", distanceKm,
            "estimatedCO2kg", emissionKg
        ));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", "Failed to estimate CO₂: " + e.getMessage()));
    }
}

}
