package com.ecober.adapter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Location;
import com.ecober.domain.service.Co2AnalyticsService;
import com.ecober.domain.service.DriverMatchingService;
import com.ecober.domain.service.RiderService;
import com.ecober.domain.service.RouteOptimizingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RiderService riderService;

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@Valid @RequestBody RiderDTO riderDTO) {
        DriverDTO matchedDriver = driverMatchingService.fetchNearestDriver(
                riderDTO.getRiderId(),
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

    @GetMapping("/distanceDuration/{riderId}")
    public ResponseEntity<DistanceDurationDTO> requestDistanceDuration(@PathVariable String riderId) {
        // Replace with actual logic using riderId if needed
        double pickupLat = 12.9352;
        double pickupLong = 77.6245;
        double dropoffLat = 12.9716;
        double dropoffLong = 77.5946;

        Location pickup = new Location(pickupLat, pickupLong, "Koramangala", 0.0);
        Location dropoff = new Location(dropoffLat, dropoffLong, "MG Road", 0.0);

        DistanceDurationDTO dto = routeOptimizingService.getDistanceAndETA(pickup, dropoff);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/getTrips/{riderId}")
    public ResponseEntity<List<TripDTO>> requestAllTrips(@PathVariable String riderId) {
        List<TripDTO> trips = riderService.fetchAllTrips(riderId);
        return ResponseEntity.ok(trips);
    }
}
