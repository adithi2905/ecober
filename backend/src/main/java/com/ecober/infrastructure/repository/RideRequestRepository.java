package com.ecober.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.RideRequestStatus;

public interface RideRequestRepository extends JpaRepository<RideRequest, UUID> {

    @Query("SELECT r FROM RideRequest r " +
           "WHERE r.pickupLocation = :location " +
           "AND r.preferredVehicleType = :vehicleType " +
           "AND r.status = :status")
    List<RideRequest> findNearbyRideRequests(
        @Param("location") String location,
        @Param("vehicleType") String vehicleType,
        @Param("status") RideRequestStatus status
    );
}
