package com.ecober.adapter.controller;

import java.nio.file.AccessDeniedException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import lombok.extern.slf4j.Slf4j;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/driver")
@Slf4j
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
@PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> startTrip() {
        log.info("🚗 Start trip endpoint called");
        
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();
            
            log.info("👤 Driver ID: {}", driverId);
            log.info("🎭 Current role: '{}'", role);
            
            // Fix: Check for both possible role formats
            boolean isDriver = "ROLE_DRIVER".equals(role) || "DRIVER".equals(role);
            
            log.info("✅ Is driver check: {}", isDriver);
            
            if (!isDriver) {
                log.warn("❌ Access denied. Expected DRIVER role, got: {}", role);
                return ResponseEntity.status(403).body("Only drivers can perform this action. Current role: " + role);
            }

            boolean started = driverService.startTrip(driverId);
            log.info("🚀 Trip started: {}", started);
            
            return started
                ? ResponseEntity.ok("Trip started successfully")
                : ResponseEntity.status(404).body("No ACCEPTED trip found for this driver.");
                
        } catch (Exception e) {
            log.error("💥 Error in start-trip: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/end-trip/{tripId}")
    public ResponseEntity<?> endTrip(@PathVariable UUID tripId) {
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();
            
            log.info("🏁 End trip called by driver: {} with role: {}", driverId, role);
            
            // Fix: Check for both possible role formats
            boolean isDriver = "ROLE_DRIVER".equals(role) || "DRIVER".equals(role);
            
            if (!isDriver) {
                log.warn("❌ Access denied for end-trip. Expected DRIVER role, got: {}", role);
                return ResponseEntity.status(403).body("Only drivers can perform this action. Current role: " + role);
            }
            
            boolean ended = driverService.endTrip(tripId, driverId);
            return ended
                    ? ResponseEntity.ok("Trip ended successfully")
                    : ResponseEntity.status(403).body("Unauthorized or invalid trip");
                    
        } catch (Exception e) {
            log.error("💥 Error in end-trip: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
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
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();
            
            log.info("🔍 Get ongoing trip ID called by driver: {} with role: {}", driverId, role);

            // Fix: Check for both possible role formats
            boolean isDriver = "ROLE_DRIVER".equals(role) || "DRIVER".equals(role);
            
            if (!isDriver) {
                log.warn("❌ Access denied for ongoing-trip-id. Expected DRIVER role, got: {}", role);
                throw new AccessDeniedException("Only drivers can perform this action. Current role: " + role);
            }

            return tripService.getOngoingTripId(driverId)
                    .<ResponseEntity<?>>map(tripId -> ResponseEntity.ok(Map.of("tripId", tripId)))
                    .orElseGet(() -> ResponseEntity.status(404).body("No ongoing trip found"));
                    
        } catch (AccessDeniedException e) {
            throw e; // Re-throw AccessDeniedException
        } catch (Exception e) {
            log.error("💥 Error in get-ongoing-trip-id: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DriverRegistrationRequestDTO request) {
        driverService.register(request);
        return ResponseEntity.ok("Driver registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DriverAuthenticationRequest request) {
        log.info("🔐 Driver login attempt for: {}", request.getEmail());
        
        try {
            Driver driver = driverService.authenticate(request);
            log.info("✅ Driver authenticated: {}", driver.getDriverId());
            
            String token = jwtService.generateToken(driver.getDriverId(), "DRIVER");
            log.info("🎫 Token generated for driver with role: DRIVER");
            
            return ResponseEntity.ok(Map.of(
                "token", token, 
                "role", "DRIVER",
                "driverId", driver.getDriverId().toString()
            ));
            
        } catch (Exception e) {
            log.error("❌ Driver login failed: {}", e.getMessage());
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

    // Debug endpoint to check authentication
    @GetMapping("/debug/auth-info")
    public ResponseEntity<?> getDriverAuthInfo() {
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();
            
            Map<String, Object> info = new HashMap<>();
            info.put("driverId", driverId.toString());
            info.put("role", role);
            info.put("roleWithPrefix", "ROLE_" + role);
            info.put("isDriverCheck", "ROLE_DRIVER".equals(role) || "DRIVER".equals(role));
            
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            return ResponseEntity.status(403).body("Auth error: " + e.getMessage());
        }
    }
}