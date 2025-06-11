package com.ecober.adapter.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.domain.model.Driver;
import com.ecober.domain.service.DriverService;
import com.ecober.security.JwtService;
import com.ecober.util.AuthUtil;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/all")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/me")
    public ResponseEntity<DriverDTO> getMyProfile() {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.getDriverById(driverId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<DriverDTO> updateMyProfile(@RequestBody DriverDTO driverDTO) {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.updateDriver(driverId, driverDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount() {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.deleteDriver(driverId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/me/carbon-impact")
    public ResponseEntity<Double> getMyCarbonImpact() {
        UUID driverId = AuthUtil.getCurrentUserId();
        return ResponseEntity.ok(driverService.calculateDriverCO2Impact(driverId));
    }

    @GetMapping("/me/trip-count")
    public ResponseEntity<Long> getMyTripCount() {
        UUID driverId = AuthUtil.getCurrentUserId();
        return ResponseEntity.ok(driverService.getDriverTripCount(driverId));
    }

    // Public endpoints
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DriverRegistrationRequestDTO request) {
        driverService.register(request);
        return ResponseEntity.ok("Driver registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DriverAuthenticationRequest request) {
        Driver driver = driverService.authenticate(request);
        String token = jwtService.generateToken(driver.getDriverId());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
