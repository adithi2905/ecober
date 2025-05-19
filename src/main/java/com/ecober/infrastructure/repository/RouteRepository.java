package com.ecober.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Route;

public interface RouteRepository extends JpaRepository<Route,String>{

    Optional<Route> findBySourceAndDestination(String riderPickupLocation, String riderDropOffLocation);


    
}
