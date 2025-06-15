package com.ecober.domain.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.RideRequestStatus;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.RideRequestRepository;
import com.ecober.infrastructure.repository.UserRepository;
import com.ecober.util.GeoUtils;

@Service
public class DriverMatchingService {

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(DriverRegistrationRequestDTO request) {
        Driver driver = new Driver();
        driver.setDriverName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driverRepository.save(driver);
    }

    public Driver authenticate(DriverAuthenticationRequest request) {
        Driver driver = driverRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return driver;
    }

    public void broadcastRideRequest(RideRequestDTO dto, UUID riderId) {
        if (riderId == null) {
            throw new IllegalStateException("Rider is not logged in or session expired.");
        }

        User user = userRepository.findById(riderId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (dto.getPickupLocation() == null || dto.getDropoffLocation() == null) {
            throw new IllegalArgumentException("Pickup and dropoff locations must not be null.");
        }

        RideRequest rideRequest = RideRequest.builder()
                .user(user)
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .preferredVehicleType(dto.getPreferredVehicleType())
                .willingToPool(dto.isWillingToPool())
                .requestedTime(LocalDateTime.now())
                .status(RideRequestStatus.REQUESTED)
                .build();

        rideRequestRepository.save(rideRequest);

        double[] pickupLatLong = geocodingService.getLatAndLong(dto.getPickupLocation());

        List<Driver> nearbyDrivers = findTopNearbyDrivers(
                pickupLatLong[0],
                pickupLatLong[1],
                dto.getPreferredVehicleType(),
                4
        );

        for (Driver driver : nearbyDrivers) {
            notificationService.notifyDriver(driver.getDriverId(),
                    "New ride request from " + dto.getPickupLocation() + " to " + dto.getDropoffLocation());
        }
    }

    public List<Driver> findTopNearbyDrivers(double pickupLat, double pickupLng, String vehicleType, int limit) {
        List<Driver> allDrivers = driverRepository.findAll();

        return allDrivers.stream()
                .filter(d -> vehicleType.equalsIgnoreCase(d.getVehicleType()))
                .sorted(Comparator.comparingDouble(d -> {
                    double[] coords = geocodingService.getLatAndLong(d.getDriverLocation());
                    return GeoUtils.haversinDistance(pickupLat, pickupLng, coords[0], coords[1]);
                }))
                .limit(limit)
                .toList();
    }
}
