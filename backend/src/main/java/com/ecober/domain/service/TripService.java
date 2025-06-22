package com.ecober.domain.service;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.TripperMapper;
import com.ecober.domain.model.*;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate<String, UUID> redisTemplate;

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
        trip.setVehicleType(bestDriver.getVehicleType());
        tripRepository.save(trip);
    }

    public List<TripDTO> fetchAllTrips(UUID riderId) {
        List<Trip> trips = tripRepository.findCompletedTripsForUser(riderId,TripStatus.COMPLETED);
        return tripMapper.toDtoList(trips);
    }
    public List<TripDTO>fetchAllDriverTrips(UUID driverUuid)
    {
        List<Trip>trips= tripRepository.findCompletedTripsWithUser(driverUuid, TripStatus.COMPLETED);
        return tripMapper.toDtoList(trips);
    }

    public boolean startTrip(UUID driverId) {
        Trip trip = tripRepository.findAcceptedRide(driverId);
        if (trip != null) {
            trip.setStartTime(LocalDateTime.now());
            trip.setStatus(TripStatus.IN_PROGRESS);
            tripRepository.save(trip);
            return true;
        }
        return false;
    }

    public TripDTO fetchCurrentTrip(UUID riderId) {
        String redisKey = "rider:currentTrip:" + riderId;
    UUID cachedTripId = redisTemplate.opsForValue().get(redisKey);
    
    if (cachedTripId != null) {
        Optional<TripDTO> cachedTrip = getTripById(cachedTripId);
        if (cachedTrip.isPresent()) return cachedTrip.get();
    }
        Trip currentTrip = tripRepository.findCurrentTrip(riderId,TripStatus.ACCEPTED,TripStatus.IN_PROGRESS);
        return (currentTrip != null) ? tripMapper.toDto(currentTrip) : null;
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

    public Trip getTripByIdForUser(UUID tripId, UUID userId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip != null && trip.getUser() != null && trip.getUser().getUserId().equals(userId)) {
            return trip;
        }
        return null;
    }

    public Optional<TripDTO> getTripById(UUID tripId) {
        Trip trip = tripRepository.findByTripId(tripId);
        return (trip != null) ? Optional.of(tripMapper.toDto(trip)) : Optional.empty();
    }
}
