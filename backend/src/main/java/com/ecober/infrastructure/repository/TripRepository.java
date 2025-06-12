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
    List<Trip> findByUserId(UUID userId);
    List<Trip> findByDriverId(UUID driverId);
    Trip findByTripId(UUID tripId);

    @Query("SELECT t FROM Trip t WHERE t.driverId = :driverId AND t.status = 'IN_PROGRESS'")
    Optional<Trip> findOngoingTripByDriverId(@Param("driverId") UUID driverId);

    List<Trip> findByDriverIdAndStatus(UUID driverId, TripStatus status);

}
