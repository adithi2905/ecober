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
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
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

    
    @GetMapping("/me/getProfile")
    public ResponseEntity<DriverDTO> getMyProfile() {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.getDriverById(driverId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me/update")
    public ResponseEntity<DriverDTO> updateMyProfile(@RequestBody DriverDTO driverDTO) {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.updateDriver(driverId, driverDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PostMapping("/end-trip/{tripId}")
    public ResponseEntity<?> endTrip(@PathVariable UUID tripId) {
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();
            boolean isDriver = "ROLE_DRIVER".equals(role) || "DRIVER".equals(role);

            if (!isDriver) {
                return ResponseEntity.status(403).body("Only drivers can perform this action. Current role: " + role);
            }

            boolean ended = driverService.endTrip(tripId, driverId);
            return ended
                    ? ResponseEntity.ok("Trip ended successfully")
                    : ResponseEntity.status(403).body("Unauthorized or invalid trip");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/me/delete")
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
    
   @GetMapping("/trip/{tripId}")
public ResponseEntity<?> getTripDetails(@PathVariable UUID tripId) {
    try {
        Optional<TripDTO> tripOpt = tripService.getTripById(tripId);

        if (tripOpt.isPresent()) {
            return ResponseEntity.ok(tripOpt.get());
        } else {
            return ResponseEntity.status(404).body("Trip not found");
        }
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error retrieving trip: " + e.getMessage());
    }
}

@GetMapping("/me/current-trip")
public ResponseEntity<?> getCurrentTrip() {
    UUID driverId = AuthUtil.getCurrentUserId();
    Optional<TripDTO> tripOpt = driverService.getCurrentTripForDriver(driverId);

    if (tripOpt.isPresent()) {
        return ResponseEntity.ok(tripOpt.get());
    } else {
        return ResponseEntity.status(404).body("No current trip found");
    }
}


@PostMapping("/start-trip/{tripId}")
public ResponseEntity<?> startTrip(@PathVariable UUID tripId) {
    UUID driverId = AuthUtil.getCurrentUserId();
    boolean success = driverService.startTrip(tripId, driverId);
    return success ? ResponseEntity.ok("Trip started") : ResponseEntity.status(403).body("Cannot start this trip");
}




    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DriverRegistrationRequestDTO request) {
        driverService.register(request);
        return ResponseEntity.ok("Driver registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DriverAuthenticationRequest request) {
        try {
            Driver driver = driverService.authenticate(request);
            String token = jwtService.generateToken(driver.getDriverId(), "DRIVER");
            return ResponseEntity.ok(Map.of(
                "token", token,
                "role", "DRIVER",
                "driverId", driver.getDriverId().toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Authentication failed: " + e.getMessage());
        }
    }

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

    @GetMapping("/tripsHistory")
    public ResponseEntity<?> fetchAllRideRequestDTOs()
    {
        try
        {
        UUID driverUuid=AuthUtil.getCurrentUserId();
        String role=AuthUtil.getCurrentUserRole();
        if((driverUuid!=null)&&(("DRIVER".equals(role))||("ROLE_DRIVER".equals(role))))
        {
        List<RideRequestDTO>availableRides=driverService.getNearbyAvailableTrips(driverUuid);
        return ResponseEntity.ok(availableRides);
        }
        else
        {
            return ResponseEntity.status(403).body("User is not a valid driver");
        }
        }
        catch(Exception ex)
        {
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }

    @PostMapping("/acceptRide/{rideRequestId}")
    public ResponseEntity<?> acceptRide(@PathVariable UUID rideRequestId) {
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            String role = AuthUtil.getCurrentUserRole();

            if (driverId == null || !"DRIVER".equalsIgnoreCase(role)) {
                return ResponseEntity.status(403).body("Only authenticated drivers can accept rides.");
            }

            TripDTO trip = driverService.acceptRide(rideRequestId, driverId);
            return ResponseEntity.ok(trip);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid ride request: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("Ride already accepted or no longer available: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error accepting ride: " + e.getMessage());
        }
    }

}
