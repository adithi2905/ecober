package com.ecober.adapter.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.domain.model.Driver;
import com.ecober.domain.service.DriverService;
import com.ecober.security.JwtService;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/all-drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverDTO> getDriverById(@PathVariable String driverId) {
        return driverService.getDriverById(driverId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<DriverDTO>> getDriversByLocation(@PathVariable String location) {
        return ResponseEntity.ok(driverService.getDriversByLocation(location));
    }

    @PostMapping("/create-drivers")
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO) {
        return ResponseEntity.ok(driverService.createDriver(driverDTO));
    }

    @PutMapping("/{driverId}")
    public ResponseEntity<DriverDTO> updateDriver(@PathVariable String driverId, @RequestBody DriverDTO driverDTO) {
        return driverService.updateDriver(driverId, driverDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String driverId) {
        return driverService.deleteDriver(driverId) 
                ? ResponseEntity.ok().build() 
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{driverId}/carbon-impact")
    public ResponseEntity<Double> getDriverCarbonImpact(@PathVariable String driverId) {
        return ResponseEntity.ok(driverService.calculateDriverCO2Impact(driverId));
    }

    @GetMapping("/{driverId}/trip-count")
    public ResponseEntity<Long> getDriverTripCount(@PathVariable String driverId) {
        return ResponseEntity.ok(driverService.getDriverTripCount(driverId));
    }

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