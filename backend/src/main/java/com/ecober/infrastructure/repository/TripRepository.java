package com.ecober.infrastructure.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecober.domain.model.Trip;

public interface TripRepository extends JpaRepository<Trip,String>{
    List<Trip> findByUserId(UUID userId);

    List<Trip> findByDriverId(String driverid);
    Trip findByTripId(UUID tripId);

}
