package com.ecober.adapter.controller;

import java.time.LocalDate;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Driver;
import com.ecober.domain.service.DriverScoringService;
import com.ecober.domain.service.DriverService;
import com.ecober.domain.service.FuelScoringService;
import com.ecober.domain.service.TripService;
import com.ecober.security.JwtService;
import com.ecober.util.AuthUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/driver")
@Slf4j
@Tag(name = "Driver APIs", description = "Operations related to driver registration, authentication, trip management, and eco analytics")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TripService tripService;

    @Autowired
    private DriverScoringService driverScoringService;

    @Autowired
    private FuelScoringService fuelScoringService;

    @Operation(summary = "Get driver profile", description = "Fetches profile details of the currently authenticated driver")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @GetMapping("/me/getProfile")
    public ResponseEntity<DriverDTO> getMyProfile() {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.getDriverById(driverId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update driver profile", description = "Updates the authenticated driver's profile details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver profile updated successfully"),
        @ApiResponse(responseCode = "404", description = "Driver not found")
    })
    @PutMapping("/me/update")
    public ResponseEntity<DriverDTO> updateMyProfile(@RequestBody DriverDTO driverDTO) {
        UUID driverId = AuthUtil.getCurrentUserId();
        return driverService.updateDriver(driverId, driverDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "End active trip", description = "Marks the specified trip as completed for the authenticated driver")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trip ended successfully"),
        @ApiResponse(responseCode = "403", description = "Unauthorized or invalid trip"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/end-trip/{tripId}")
    public ResponseEntity<?> endTrip(@PathVariable UUID tripId) {
        try {
            UUID driverId = AuthUtil.getCurrentUserId();
            boolean ended = driverService.endTrip(tripId, driverId);
            if (ended) {
                return ResponseEntity.ok("Trip ended successfully");
            } else {
                return ResponseEntity.status(403).body("Unauthorized or invalid trip");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @Operation(summary = "Register driver", description = "Registers a new driver with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody DriverRegistrationRequestDTO request) {
        driverService.register(request);
        return ResponseEntity.ok("Driver registered");
    }

    @Operation(summary = "Driver login", description = "Authenticates a driver and generates JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Driver authenticated successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication failed")
    })
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

    @Operation(summary = "Fetch nearby available trips", description = "Retrieves a list of ride requests near the driver location")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Nearby trips retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Only authenticated drivers can view trips")
    })
    @GetMapping("/fetchRides")
    public ResponseEntity<?> getNearbyAvailableTrips() {
        UUID driverId = AuthUtil.getCurrentUserId();
        List<RideRequestDTO> trips = driverService.getNearbyAvailableTrips(driverId);
        return ResponseEntity.ok(trips);
    }

    @Operation(summary = "Get eco report", description = "Generates a detailed eco report for the driver")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Eco report generated successfully"),
        @ApiResponse(responseCode = "500", description = "Error generating eco report")
    })
    @GetMapping("/me/eco-report")
    public ResponseEntity<?> getEcoReport() {
        UUID driverId = AuthUtil.getCurrentUserId();
        double totalCO2 = driverService.calculateDriverCO2Impact(driverId);
        Map<String, Long> tripCount = driverService.getDriverTripCounts(driverId);
        double carbonScore = driverScoringService.calculateCarbonCost(totalCO2, tripCount.get("totalRides"));
        String rating = driverService.getEcoBadge(driverId);
        double currentMonthCO2 = driverService.getCurrentMonthCO2Savings(driverId);
        List<Map<String, Object>> rideDistribution = driverService.getRideTypeDistribution(driverId);

        Map<String, Object> report = new HashMap<>();
        report.put("totalCO2", totalCO2);
        report.put("tripCount", tripCount);
        report.put("carbonScore", carbonScore);
        report.put("carbonRating", rating);
        report.put("monthlyCo2Savings", List.of(Map.of(
                "month", LocalDate.now().getMonth().toString().substring(0, 3),
                "co2", currentMonthCO2
        )));
        report.put("rideTypeDistribution", rideDistribution);

        return ResponseEntity.ok(report);
    }
}
