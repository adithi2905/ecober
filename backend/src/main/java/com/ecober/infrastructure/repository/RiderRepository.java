package com.ecober.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecober.domain.model.Rider;

@Repository
public interface RiderRepository extends JpaRepository<Rider, String> {
    
    Optional<Rider> findByRiderId(String riderId);
    
    List<Rider> findByRiderPickupLocation(String pickupLocation);
    
    List<Rider> findByWillingToPool(boolean willingToPool);
    
    List<Rider> findByPreferredVehicleType(String vehicleType);
}
