package com.ecober.adapter.controller;

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
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.domain.service.Co2AnalyticsService;
import com.ecober.domain.service.DriverMatchingService;

import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.constraints.NotNull;
@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    DriverMatchingService fetchNearestRiderService;

    @Autowired
    Co2AnalyticsService co2AnalyticsService;

    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@NotNull @RequestBody RiderDTO riderDTO)
    {
        DriverDTO matchedDriver=fetchNearestRiderService.fetchNearestDriver(riderDTO.getRiderId(),riderDTO.getRiderPickupLocation(),riderDTO.getRiderDropOffLocation(),riderDTO.getPickupLatitude(),riderDTO.getPickupLongitude(),riderDTO.getDropoffLatitude(),riderDTO.getDropoffLongitude(),riderDTO.getPreferredVehicleType(),riderDTO.isWillingToPool());
        return ResponseEntity.ok(matchedDriver);
    }

    @GetMapping("/carbonEmission/{riderId}")
    public ResponseEntity<CarbonDTO> requestCarbonEmission(@PathVariable String riderId)
    {
        CarbonDTO carbonDTO=co2AnalyticsService.getRiderCarbonEmission(riderId);
        return ResponseEntity.ok(carbonDTO);
    }
}
