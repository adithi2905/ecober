package com.ecober.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecober.domain.model.Trip;

public interface TripRepository extends JpaRepository<Trip,String>{
    List<Trip> findByUserId(String userId);

    List<Trip> findByDriverId(String driverId);
    
}
