package com.ecober.domain.service;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.TripperMapper;
import com.ecober.domain.model.*;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TripService {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TripperMapper tripMapper;

    public void createTrip(UUID riderId, Driver bestDriver, Route route, double carbonEmission) {
        User user = userRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + riderId));

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setDriver(bestDriver);
        trip.setRoute(route);
        trip.setStartTime(LocalDateTime.now());
        trip.setEstimatedEmission(carbonEmission);
        trip.setEcoScore("B+");
        trip.setStatus(TripStatus.ACCEPTED);

        tripRepository.save(trip);
    }

    public List<TripDTO> fetchAllTrips(UUID riderId) {
        List<Trip> trips = tripRepository.findByUser_UserId(riderId);
        return tripMapper.toDtoList(trips);
    }

    public boolean startTrip(UUID driverId) {
        Optional<Trip> tripOpt = tripRepository.findByDriver_DriverIdAndStatus(driverId, TripStatus.ACCEPTED)
                                               .stream().findFirst();
        if (tripOpt.isPresent()) {
            Trip trip = tripOpt.get();
            trip.setStartTime(LocalDateTime.now());
            trip.setStatus(TripStatus.IN_PROGRESS);
            tripRepository.save(trip);
            return true;
        }
        return false;
    }

    public boolean endTrip(UUID tripId, UUID driverId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip == null || !driverId.equals(trip.getDriver().getDriverId())) {
            return false;
        }
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            return false;
        }
        trip.setStatus(TripStatus.COMPLETED);
        trip.setEndTime(LocalDateTime.now());
        tripRepository.save(trip);
        return true;
    }

    public Optional<UUID> getOngoingTripId(UUID driverId) {
        return tripRepository.findOngoingTripByDriverId(driverId)
                             .map(Trip::getTripId);
    }
}
