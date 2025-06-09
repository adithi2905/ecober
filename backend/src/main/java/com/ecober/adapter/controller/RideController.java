package com.ecober.adapter.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.service.DriverMatchingService;
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
    private DriverMatchingService driverMatchingService;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@Valid @RequestBody RiderDTO riderDTO) {
        UUID riderId = AuthUtil.getCurrentUserId();
        if (riderId == null) {
            return ResponseEntity.status(401).build();
        }

        riderDTO.setRiderId(riderId);

        DriverDTO matchedDriver = driverMatchingService.fetchNearestDriver(
                riderId,
                riderDTO.getRiderPickupLocation(),
                riderDTO.getRiderDropOffLocation(),
                riderDTO.getPickupLatitude(),
                riderDTO.getPickupLongitude(),
                riderDTO.getDropoffLatitude(),
                riderDTO.getDropoffLongitude(),
                riderDTO.getPreferredVehicleType(),
                riderDTO.isWillingToPool()
        );

        return ResponseEntity.ok(matchedDriver);
    }

    @GetMapping("/distanceDuration/{tripId}")
    public ResponseEntity<DistanceDurationDTO> requestDistanceDuration(@PathVariable UUID tripId) {
        UUID riderId = AuthUtil.getCurrentUserId();
        if (riderId == null) {
            return ResponseEntity.status(401).body(null);
        }

        Trip userTrip = tripRepository.findByTripId(tripId);
        if (userTrip == null) {
            return ResponseEntity.status(404).body(null);
        }

        if (!userTrip.getUser().getId().equals(riderId)) {
            return ResponseEntity.status(403).body(null);
        }

        Route route = userTrip.getRoute();
        DistanceDurationDTO dto = routeOptimizingService.getDistanceAndETA(route.getSource(), route.getDestination());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getTrips")
    public ResponseEntity<List<TripDTO>> requestAllTrips() {
        UUID userId = AuthUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }

        List<TripDTO> trips = tripService.fetchAllTrips(userId);
        return ResponseEntity.ok(trips);
    }
}
