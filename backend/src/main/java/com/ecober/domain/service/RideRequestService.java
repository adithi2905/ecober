package com.ecober.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.RideRequestStatus;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.RideRequestRepository;
import com.ecober.infrastructure.repository.UserRepository;

@Service
public class RideRequestService {

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    GeocodingService geocodingService;

    @Autowired
    private NotificationService notificationService;

    public DriverDTO processRideRequest(RideRequestDTO dto, UUID userId) {
        if (userId == null) {
            throw new IllegalStateException("Rider is not logged in or session expired.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));
        
        if (dto.getPickupLocation() == null || dto.getDropoffLocation() == null) {
    throw new IllegalArgumentException("Pickup and dropoff locations must not be null.");
}


        RideRequest request = mapToRideRequest(dto, user);
        rideRequestRepository.save(request);

        double[]pickuplatlong=geocodingService.getLatAndLong(dto.getPickupLocation());
        double[]dropofflatlong=geocodingService.getLatAndLong(dto.getDropoffLocation());

        DriverDTO matchedDriver = driverMatchingService.fetchNearestDriver(
                userId,
                dto.getPickupLocation(),
                dto.getDropoffLocation(),
                pickuplatlong[0],
                pickuplatlong[1],
                dropofflatlong[0],
                dropofflatlong[1],
                dto.getPreferredVehicleType(),
                dto.isWillingToPool()
        );

        if (matchedDriver != null) {
            notificationService.notifyRider(userId, "Driver found: " + matchedDriver.getDriverName());
            notificationService.notifyDriver(matchedDriver.getDriverId(), "New ride request from your area.");
        } else {
            notificationService.notifyRider(userId, "No available drivers right now. Try again later.");
        }

        return matchedDriver;
    }

    public void cancelRideRequest(UUID userId, String reason) {
        notificationService.notifyRider(userId, "Ride cancelled: " + reason);
    }

    public String generateRideId() {
        return "RIDE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private RideRequest mapToRideRequest(RideRequestDTO dto, User user) {
        return RideRequest.builder()
                .user(user)
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .preferredVehicleType(dto.getPreferredVehicleType())
                .willingToPool(dto.isWillingToPool())
                .requestedTime(LocalDateTime.now())
                .status(RideRequestStatus.REQUESTED)
                .build();
    }
}
