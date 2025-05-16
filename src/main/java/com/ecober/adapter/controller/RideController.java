package com.ecober.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.domain.service.FetchNearestRiderService;

@Controller
public class RideController {

    @Autowired
    FetchNearestRiderService fetchNearestRiderService;
    @PostMapping("/requestRide")
    public ResponseEntity<DriverDTO> requestRide(@RequestParam RiderDTO riderDTO)
    {
        DriverDTO matchedDriver=fetchNearestRiderService.fetchNearestRider(riderDTO.getRiderPickupLocation(),riderDTO.getRiderDropOffLocation(),riderDTO.getPreferredVehicleType(),riderDTO.isWilingToPool());
        return ResponseEntity.ok(matchedDriver);
    }

    
    
}
