package com.ecober.domain.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.domain.model.Rider;
import com.ecober.infrastructure.repository.RiderRepository;

@Service
public class RideRequestService {
    
    @Autowired
    private DriverMatchingService driverMatchingService;
    
    @Autowired
    private RiderRepository riderRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    public DriverDTO processRideRequest(RiderDTO riderDTO) {
        // Save or update rider information
        Rider rider = mapToRider(riderDTO);
        riderRepository.save(rider);
        
        // Find and match driver
        DriverDTO matchedDriver = driverMatchingService.fetchNearestDriver(
            riderDTO.getRiderId(),
            riderDTO.getRiderPickupLocation(),
            riderDTO.getRiderDropOffLocation(),
            riderDTO.getPickupLatitude(),
            riderDTO.getPickupLongitude(),
            riderDTO.getDropoffLatitude(),
            riderDTO.getDropoffLongitude(),
            riderDTO.getPreferredVehicleType(),
            riderDTO.isWillingToPool()
        );
        
        // Send notifications
        notificationService.notifyRider(riderDTO.getRiderId(), "Driver found: " + matchedDriver.getDriverName());
        notificationService.notifyDriver(matchedDriver.getDriverId(), "New ride request from " + riderDTO.getRiderName());
        
        return matchedDriver;
    }
    
    public void cancelRideRequest(String riderId, String reason) {
        // Implement ride cancellation logic
        notificationService.notifyRider(riderId, "Ride cancelled: " + reason);
    }
    
    public String generateRideId() {
        return "RIDE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private Rider mapToRider(RiderDTO riderDTO) {
        Rider rider = new Rider();
        rider.setRiderId(riderDTO.getRiderId());
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