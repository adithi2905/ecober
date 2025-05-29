package com.ecober.adapter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.*;
import com.ecober.domain.service.*;


@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    private RiderService riderService;

    @Autowired
    private Co2AnalyticsService co2AnalyticsService;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    @Autowired
    private RiderRequestService riderRequestService;

    @PostMapping("/requestRide")
public ResponseEntity<RideResponseDTO> requestRide(@RequestBody RiderDTO riderDTO) {
    RideResponseDTO response = riderRequestService.handleRideRequest(riderDTO);
    return ResponseEntity.ok(response);
}
@GetMapping("/ping")
public String ping() {
    return "RideController is alive!";
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

    @GetMapping("/carbonEmission/{riderId}")
    public ResponseEntity<CarbonDTO> requestCarbonEmission(@PathVariable String riderId) {
        CarbonDTO carbonDTO = co2AnalyticsService.getRiderCarbonEmission(riderId);
        return ResponseEntity.ok(carbonDTO);
    }

    @GetMapping("/riderService/getTrips/{riderId}")
    public ResponseEntity<List<TripDTO>> requestAllTrips(@PathVariable String riderId) {
        List<TripDTO> trips = riderService.fetchAllTrips(riderId);
        return ResponseEntity.ok(trips);
    }
}
