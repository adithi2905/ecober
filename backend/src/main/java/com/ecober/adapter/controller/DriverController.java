package com.ecober.adapter.controller;

import java.nio.file.AccessDeniedException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.domain.model.Driver;
import com.ecober.domain.service.DriverService;
import com.ecober.domain.service.TripService;
import com.ecober.security.JwtService;
import com.ecober.util.AuthUtil;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TripService tripService;

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

    @PostMapping("/start-trip")
public ResponseEntity<?> startTrip() {
    UUID driverId = AuthUtil.getCurrentUserId();
    String role = AuthUtil.getCurrentUserRole();

    if (!"ROLE_DRIVER".equals(role)) {
        return ResponseEntity.status(403).body("Only drivers can perform this action");
    }

    boolean started = driverService.startTrip(driverId);
    return started
        ? ResponseEntity.ok("Trip started successfully")
        : ResponseEntity.status(404).body("No ACCEPTED trip found for this driver.");
}

    @PostMapping("/end-trip/{tripId}")
    public ResponseEntity<?> endTrip(@PathVariable UUID tripId) {
        UUID driverId = AuthUtil.getCurrentUserId();
        boolean ended = driverService.endTrip(tripId, driverId);
        return ended
                ? ResponseEntity.ok("Trip ended successfully")
                : ResponseEntity.status(403).body("Unauthorized or invalid trip");
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

    @GetMapping("/me/ongoing-trip-id")
    public ResponseEntity<?> getOngoingTripId() throws AccessDeniedException {
        UUID driverId = AuthUtil.getCurrentUserId();
        String role = AuthUtil.getCurrentUserRole();

        if (!"ROLE_DRIVER".equals(role)) {
            throw new AccessDeniedException("Only drivers can perform this action");
        }

        return tripService.getOngoingTripId(driverId)
                .<ResponseEntity<?>>map(tripId -> ResponseEntity.ok(Map.of("tripId", tripId)))
                .orElseGet(() -> ResponseEntity.status(404).body("No ongoing trip found"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DriverRegistrationRequestDTO request) {
        driverService.register(request);
        return ResponseEntity.ok("Driver registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DriverAuthenticationRequest request) {
        Driver driver = driverService.authenticate(request);
        String token = jwtService.generateToken(driver.getDriverId(), "DRIVER");
        return ResponseEntity.ok(Map.of("token", token, "role", "DRIVER"));
    }
}
