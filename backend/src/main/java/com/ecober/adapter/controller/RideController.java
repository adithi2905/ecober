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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/ride")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Ride APIs", description = "APIs for requesting rides and estimating emissions")
public class RideController {

    @Autowired
    private TripService tripService;

    @Autowired
    private RideRequestService rideRequestService;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    private EmissionUtils emissionUtils;

    @Operation(summary = "Request a ride", description = "Allows a rider to request a ride and broadcast it to nearby drivers.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ride request broadcasted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "403", description = "Only authenticated riders can request rides"),
        @ApiResponse(responseCode = "500", description = "Error processing ride request")
    })
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

    @Operation(summary = "Get distance and duration for a trip", description = "Fetches the distance and estimated duration for a specific trip.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Distance and duration fetched successfully", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = DistanceDurationDTO.class))),
        @ApiResponse(responseCode = "403", description = "Unauthorized or invalid trip"),
        @ApiResponse(responseCode = "500", description = "Failed to calculate distance/ETA")
    })
    @GetMapping("/distanceDuration/{tripId}")
    public ResponseEntity<?> requestDistanceDuration(
            @Parameter(description = "UUID of the trip", required = true) @PathVariable UUID tripId) {
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

    @Operation(summary = "Estimate CO₂ emissions", description = "Estimates CO₂ emissions for a given distance and vehicle type.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Emission estimated successfully"),
        @ApiResponse(responseCode = "500", description = "Failed to estimate CO₂ emissions")
    })
    @GetMapping("/emissionEstimate")
    public ResponseEntity<?> estimateEmission(
            @Parameter(description = "Distance in kilometers", required = true) @RequestParam double distanceKm,
            @Parameter(description = "Type of vehicle (e.g., Sedan, SUV)", required = true) @RequestParam String vehicleType
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
