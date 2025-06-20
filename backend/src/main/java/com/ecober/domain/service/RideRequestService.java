package com.ecober.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.RideRequestStatus;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.model.TripStatus;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.RideRequestRepository;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.infrastructure.repository.UserRepository;
import com.ecober.util.GeoUtils;

@Service
public class RideRequestService {

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private BroadcastingService broadcastingService;

    public void processRideRequest(RideRequestDTO dto, UUID userId) {
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

        double[] pickupLatLong = geocodingService.getLatAndLong(dto.getPickupLocation());

        List<DriverDTO> drivers = broadcastingService.findAndNotifyTopDrivers(
                pickupLatLong[0],
                pickupLatLong[1],
                dto.getPreferredVehicleType(),
                4
        );

        if (drivers.isEmpty()) {
            notificationService.notifyRider(userId, "No available drivers right now. Try again later.");
        } else {
            for (DriverDTO driver : drivers) {
                notificationService.notifyDriver(driver.getDriverId(), 
                    "New ride request from " + dto.getPickupLocation() + " to " + dto.getDropoffLocation());
            }
            notificationService.notifyRider(userId, "Ride request sent to nearby drivers.");
        }
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
