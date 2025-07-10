package com.ecober.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.ecober.adapter.Dto.LoginRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.Dto.UserDTO;
import com.ecober.adapter.Dto.UserProfileDTO;
import com.ecober.adapter.mapper.UserMapper;
import com.ecober.domain.model.User;
import com.ecober.domain.service.TripService;
import com.ecober.domain.service.UserService;
import com.ecober.domain.service.UserRegistrationService;
import com.ecober.infrastructure.repository.UserRepository;
import com.ecober.security.JwtService;
import com.ecober.util.AuthUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User APIs", description = "APIs for rider registration, login, profile, and trip management")
public class UserController {

    @Autowired private UserMapper userMapper;
    @Autowired private JwtService jwtService;
    @Autowired private UserRegistrationService userRegService;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthenticationManager authenticateManager;
    @Autowired private TripService tripService;
    @Autowired private UserService userService;

    @Operation(summary = "Get Rider Profile", description = "Fetches the authenticated rider's profile including CO₂ metrics and badges.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDTO.class))),
        @ApiResponse(responseCode = "403", description = "Unauthorized access"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        UUID userId = AuthUtil.getCurrentUserId();
        String role = AuthUtil.getCurrentUserRole();

        if (userId == null || !"RIDER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Unauthorized access to profile"));
        }

        UserProfileDTO userProfileDTO = userService.buildUserProfile(userId);
        if (userProfileDTO != null) {
            return ResponseEntity.ok(userProfileDTO);
        } else {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "User not found"));
        }
    }

    @Operation(summary = "Register Rider", description = "Registers a new rider account.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Password is null")
    })
    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDto) {
        if (userDto.getPassword() != null) {
            User user = userMapper.toEntity(userDto);
            userRegService.createUser(user);
            return ResponseEntity.ok("User Registered Successfully");
        } else {
            return ResponseEntity.badRequest().body("Password is null");
        }
    }

    @Operation(summary = "Login Rider", description = "Authenticates a rider and returns a JWT token.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful, token returned"),
        @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO login) {
        authenticateManager.authenticate(
            new UsernamePasswordAuthenticationToken(login.getUsername(), login.getPassword())
        );

        User user = userRepository.findByUsername(login.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.generateToken(user.getUserId(), "RIDER");
        return ResponseEntity.ok(Map.of("token", token, "role", "RIDER"));
    }

    @Operation(summary = "Fetch Current Trip", description = "Gets the current active trip for the authenticated rider.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Current trip fetched successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TripDTO.class))),
        @ApiResponse(responseCode = "403", description = "User is not authenticated as a rider"),
        @ApiResponse(responseCode = "404", description = "No trips found")
    })
    @GetMapping("/trip/current")
    public ResponseEntity<?> fetchCurrentTrips() {
        UUID riderId = AuthUtil.getCurrentUserId();
        String role = AuthUtil.getCurrentUserRole();

        if (!isRider(riderId, role)) {
            return ResponseEntity.status(403).body("User is not authenticated as a rider");
        }

        TripDTO currentTrip = tripService.fetchCurrentTrip(riderId);
        return currentTrip != null
                ? ResponseEntity.ok(currentTrip)
                : ResponseEntity.status(404).body("No trips found");
    }

    @Operation(summary = "Logout Rider", description = "Logs out the rider by clearing their token on the client side.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Please clear token on client");
    }

    @Operation(summary = "Fetch Rider Trip History", description = "Gets all completed trips for the authenticated rider.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trip history fetched successfully"),
        @ApiResponse(responseCode = "500", description = "Failed to fetch trips")
    })
    @GetMapping("/tripsHistory")
    public ResponseEntity<?> getAllRiderTrips() {
        try {
            UUID userId = AuthUtil.getCurrentUserId();
            List<TripDTO> trips = tripService.fetchAllTrips(userId);
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch trips: " + e.getMessage());
        }
    }

    private boolean isRider(UUID userId, String role) {
        return userId != null && ("RIDER".equals(role) || "ROLE_RIDER".equals(role));
    }
}
