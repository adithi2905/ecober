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
import com.ecober.domain.service.RiderRequestService;
import com.ecober.domain.service.RiderService;
import com.ecober.domain.service.RouteOptimizingService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.constraints.NotNull;
@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    RiderService riderService;

    @Autowired
    DriverMatchingService fetchNearestRiderService;

    @Autowired
    Co2AnalyticsService co2AnalyticsService;

    @Autowired
    RouteOptimizingService routeOptimizingService;

    @Autowired
    private RiderRequestService riderRequestService;


    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@NotNull @RequestBody RiderDTO riderDTO) {
        DriverDTO matchedDriver = riderRequestService.handleRideRequest(riderDTO);
        return ResponseEntity.ok(matchedDriver);
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
