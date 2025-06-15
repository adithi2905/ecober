package com.ecober.adapter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecober.adapter.Dto.LoginRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.Dto.UserDTO;
import com.ecober.adapter.mapper.UserMapper;
import com.ecober.domain.model.User;
import com.ecober.domain.service.TripService;
import com.ecober.domain.service.UserLoginService;
import com.ecober.domain.service.UserRegistrationService;
import com.ecober.infrastructure.repository.UserRepository;
import com.ecober.security.JwtService;
import com.ecober.util.AuthUtil;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRegistrationService userRegService;

    @Autowired
    UserLoginService userLogin;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthenticationManager authenticateManager;

    @Autowired
    TripService tripService;

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDto) {
        if(userDto.getPassword() != null) {
            User user = userMapper.toEntity(userDto);
            userRegService.createUser(user);
            return ResponseEntity.ok("User Registered Successfully");
        } else {
            throw new IllegalArgumentException("Password is null");
        }
    }

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

    @GetMapping("/trip/current")
    public ResponseEntity<?>fetchCurrentTrips()
    {
        
        UUID riderId=AuthUtil.getCurrentUserId();
        String role=AuthUtil.getCurrentUserRole();
        boolean isRider=((riderId!=null) && ("RIDER".equals(role)||("ROLE_RIDER".equals(role))));
        if (!isRider) {
        return ResponseEntity.status(403).body("User is not authenticated as a rider");
    }
       
        TripDTO currentTrip=tripService.fetchCurrentTrip(riderId);
        
        if(currentTrip!=null)
        return ResponseEntity.ok(currentTrip);
        else
        {
            return ResponseEntity.status(404).body("No trips found");
        }
       }
           

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Please clear token on client");
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
