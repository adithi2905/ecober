package com.ecober.domain.service;

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

    public void createTrip(String riderId, String driverId, String driverName, Route route) {
    Trip trip = new Trip(riderId, driverId, driverName, route, LocalDateTime.now());
    tripRepository.save(trip);
}

}
