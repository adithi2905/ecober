package com.ecober.adapter.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.service.RideRequestService;
import com.ecober.domain.service.RouteOptimizingService;
import com.ecober.domain.service.TripService;
import com.ecober.infrastructure.repository.TripRepository;
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
    private TripRepository tripRepository;

    @Autowired
    private RideRequestService rideRequestService;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    
    @PostMapping("/requestRide")
    public ResponseEntity<?> requestRide(@Valid @RequestBody RideRequestDTO rideDTO) {
        try {
            UUID userId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();
            
            // Debug logging - remove after fixing
            System.out.println("User ID: " + userId);
            System.out.println("User Role: " + role);
            
            // Check if user is a rider - role is now normalized without ROLE_ prefix
            boolean isRider = "RIDER".equals(role);

            if (!isRider) {
                return ResponseEntity.status(403).body("Forbidden: Only riders can request rides. Current role: " + role);
            }

            DriverDTO matchedDriver = rideRequestService.processRideRequest(rideDTO, userId);
            if (matchedDriver == null) {
                return ResponseEntity.status(404).body("No driver available at the moment.");
            }

            return ResponseEntity.ok(matchedDriver);
            
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
            }
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/distanceDuration/{tripId}")
    public ResponseEntity<?> requestDistanceDuration(@PathVariable UUID tripId) {
        try {
            UUID userId = AuthUtil.getCurrentUserId();

            Trip trip = tripRepository.findByTripId(tripId);
            if (trip == null) {
                return ResponseEntity.status(404).body("Trip not found");
            }

            if (!trip.getUser().getUserId().equals(userId)) {
                return ResponseEntity.status(403).body("Forbidden: You are not the owner of this trip");
            }

            Route route = trip.getRoute();
            DistanceDurationDTO dto = routeOptimizingService.getDistanceAndETA(route.getSource(), route.getDestination());
            return ResponseEntity.ok(dto);
            
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
            }
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/getTrips")
    public ResponseEntity<?> getAllRiderTrips() {
        try {
            UUID userId = AuthUtil.getCurrentUserId();
            List<TripDTO> trips = tripService.fetchAllTrips(userId);
            return ResponseEntity.ok(trips);
            
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Unauthorized")) {
                return ResponseEntity.status(401).body("Unauthorized: " + e.getMessage());
            }
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}