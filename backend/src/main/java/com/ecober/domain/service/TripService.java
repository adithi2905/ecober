package com.ecober.domain.service;

import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.TripMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.infrastructure.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripMapper tripMapper;

    public void createTrip(UUID riderId, Driver best, Route route, double carbonEmission) {
        User user = userRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + riderId));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setDriverId(best.getDriverId());
        trip.setDriverName(best.getDriverName());
        trip.setRoute(route);
        trip.setStartTime(LocalDateTime.now());
        trip.setEstimatedEmission(carbonEmission);
        trip.setEcoScore("B+");

        tripRepository.save(trip);
    }

    public List<TripDTO> fetchAllTrips(UUID riderID) {
        List<Trip> results = tripRepository.findByUserId(riderID);
        return tripMapper.toDtoList(results);
    }

        public void startTrip(UUID tripId, UUID userId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip.getUser().getId().equals(userId)) {
            trip.setStartTime(LocalDateTime.now());
            trip.setStatus("IN_PROGRESS");
            tripRepository.save(trip);
        } else {
            throw new IllegalStateException("Not authorized");
        }
    }

    public void endTrip(UUID tripId, UUID userId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip.getUser().getId().equals(userId)) {
            trip.setEndTime(LocalDateTime.now());
            trip.setStatus("COMPLETED");
            tripRepository.save(trip);
        } else {
            throw new IllegalStateException("Not authorized");
        }
}

}
