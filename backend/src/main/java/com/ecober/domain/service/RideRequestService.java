package com.ecober.domain.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.domain.model.Rider;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.RiderRepository;
import com.ecober.infrastructure.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class RideRequestService {

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public DriverDTO processRideRequest(RiderDTO riderDTO, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("riderId");
        if (userId == null) {
            throw new IllegalStateException("Rider is not logged in or session expired.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID: " + userId));

        Rider rider = mapToRider(riderDTO);
        rider.setUser(user);
        riderRepository.save(rider);

        DriverDTO matchedDriver = driverMatchingService.fetchNearestDriver(
                userId,
                riderDTO.getRiderPickupLocation(),
                riderDTO.getRiderDropOffLocation(),
                riderDTO.getPickupLatitude(),
                riderDTO.getPickupLongitude(),
                riderDTO.getDropoffLatitude(),
                riderDTO.getDropoffLongitude(),
                riderDTO.getPreferredVehicleType(),
                riderDTO.isWillingToPool()
        );

        notificationService.notifyRider(userId, "Driver found: " + matchedDriver.getDriverName());
        notificationService.notifyDriver(matchedDriver.getDriverId(), "New ride request from " + riderDTO.getRiderName());

        return matchedDriver;
    }

    public void cancelRideRequest(UUID userId, String reason) {
        notificationService.notifyRider(userId, "Ride cancelled: " + reason);
    }

    public String generateRideId() {
        return "RIDE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private Rider mapToRider(RiderDTO riderDTO) {
        Rider rider = new Rider();
        rider.setRiderName(riderDTO.getRiderName());
        rider.setRiderPickupLocation(riderDTO.getRiderPickupLocation());
        rider.setRiderDropOffLocation(riderDTO.getRiderDropOffLocation());
        rider.setPickupLatitude(riderDTO.getPickupLatitude());
        rider.setPickupLongitude(riderDTO.getPickupLongitude());
        rider.setDropoffLatitude(riderDTO.getDropoffLatitude());
        rider.setDropoffLongitude(riderDTO.getDropoffLongitude());
        rider.setPreferredVehicleType(riderDTO.getPreferredVehicleType());
        rider.setWillingToPool(riderDTO.isWillingToPool());
        rider.setCo2Saved(riderDTO.getCo2Saved());
        return rider;
    }
}
