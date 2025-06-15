package com.ecober.adapter.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.service.RideRequestService;
import com.ecober.domain.service.RouteOptimizingService;
import com.ecober.domain.service.TripService;
import com.ecober.util.AuthUtil;

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

    @PostMapping("/requestRide")
    public ResponseEntity<?> requestRide(@Valid @RequestBody RideRequestDTO rideDTO) {
        try {
            UUID userId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();

            if (userId == null || !"RIDER".equalsIgnoreCase(role)) {
                return ResponseEntity.status(403).body("Only authenticated riders can request rides.");
            }

            rideRequestService.processRideRequest(rideDTO, userId);
            return ResponseEntity.ok("Ride request broadcasted to nearby drivers.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing ride request: " + e.getMessage());
        }
    }

    @PostMapping("/acceptRide/{rideRequestId}")
    public ResponseEntity<?> acceptRide(@PathVariable UUID rideRequestId) {
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();

            if (driverId == null || !"DRIVER".equalsIgnoreCase(role)) {
                return ResponseEntity.status(403).body("Only authenticated drivers can accept rides.");
            }

            TripDTO trip = rideRequestService.acceptRide(rideRequestId, driverId);
            return ResponseEntity.ok(trip);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid ride request: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("Ride already accepted or no longer available: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error accepting ride: " + e.getMessage());
        }
    }

    @GetMapping("/getTrips")
    public ResponseEntity<?> getAllRiderTrips() {
        try {
            UUID userId = AuthUtil.getCurrentUserId();
            List<TripDTO> trips = tripService.fetchAllTrips(userId);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch trips: " + e.getMessage());
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
}
