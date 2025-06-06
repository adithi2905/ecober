package com.ecober.domain.service;

import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

   
   public void createTrip(String riderId, Driver best, Route route, double carbonEmission) {
    Trip trip = new Trip();
    trip.setUserId(riderId);
    trip.setDriverId(best.getDriverId());
    trip.setDriverName(best.getDriverName());
    trip.setRoute(route);
    trip.setStartTime(LocalDateTime.now());
    trip.setEstimatedEmission(carbonEmission);
    trip.setEcoScore("B+");
    tripRepository.save(trip);


}

}
