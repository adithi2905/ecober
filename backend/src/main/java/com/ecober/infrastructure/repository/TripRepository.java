package com.ecober.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecober.domain.model.Trip;
import com.ecober.domain.model.TripStatus;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findByUser_UserId(UUID riderId);

    List<Trip> findByDriver_DriverId(UUID driverId);

    List<Trip> findByDriver_DriverIdAndStatus(UUID driverId, TripStatus status);

    Trip findByTripId(UUID tripId);

    @Query("SELECT t FROM Trip t WHERE t.driver.driverId = :driverId AND t.status = 'IN_PROGRESS'")
    Optional<Trip> findOngoingTripByDriverId(@Param("driverId") UUID driverId);

    @Query("SELECT t FROM Trip t WHERE t.user.userId = :riderId AND t.status = 'ACCEPTED'")
    Trip findCurrentTrip(@Param("riderId") UUID riderId);

}
