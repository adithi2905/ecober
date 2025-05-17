package com.ecober.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.domain.service.DriverMatchingService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/ride")
public class RideController {

    @Autowired
    DriverMatchingService fetchNearestRiderService;
    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@Valid @RequestBody RiderDTO riderDTO)
    {
        DriverDTO matchedDriver=fetchNearestRiderService.fetchNearestDriver(riderDTO.getRiderPickupLocation(),riderDTO.getRiderDropOffLocation(),riderDTO.getPreferredVehicleType(),riderDTO.isWilingToPool());
        return ResponseEntity.ok(matchedDriver);
    }

    
    
}
