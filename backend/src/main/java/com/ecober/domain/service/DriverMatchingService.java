package com.ecober.domain.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.RouteRepository;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.util.GeoUtils;

@Service
public class DriverMatchingService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    public DriverDTO fetchNearestDriver(String riderId,
                                        String riderPickupAddress,
                                        String riderDropoffAddress,
                                        double pickupLatitude,
                                        double pickupLongitude,
                                        double dropoffLatitude,
                                        double dropoffLongitude,
                                        String preferredVehicleType,
                                        boolean willingToPool) {

        Logger.getLogger(DriverMatchingService.class.getName()).info("Matching driver for: " + riderPickupAddress);

        // Build Location objects
        Location pickup = new Location();
        pickup.setLatitude(pickupLatitude);
        pickup.setLongitude(pickupLongitude);
        pickup.setAddress(riderPickupAddress);
        pickup.setElevation(0.0); // or fetch from an elevation API if needed

        Location dropoff = new Location();
        dropoff.setLatitude(dropoffLatitude);
        dropoff.setLongitude(dropoffLongitude);
        dropoff.setAddress(riderDropoffAddress);
        dropoff.setElevation(0.0);

        // Calculate route info
        DistanceDurationDTO distanceDurationDTO;
        try {
            distanceDurationDTO = routeOptimizingService.getDistanceAndETA(pickup, dropoff);
        } catch (Exception ex) {
            distanceDurationDTO = GeoUtils.haversinDistanceandDuration(pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude);
        }

        double carbonEmission = GeoUtils.calculateEmissions(distanceDurationDTO.getDistanceKm(), preferredVehicleType);

        // Find available drivers
        List<Driver> availableDrivers = driverRepository.findByDriverLocation(riderPickupAddress);
        Driver best = availableDrivers.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable driver found"));

        // Fetch or create route
        Route route = routeRepository.findByCoordinates(
                pickup.getLatitude(), pickup.getLongitude(),
                dropoff.getLatitude(), dropoff.getLongitude()
        ).orElseGet(() -> {
            Route newRoute = Route.builder()
                    .routeID(UUID.randomUUID().toString())
                    .source(pickup)
                    .destination(dropoff)
                    .distanceKm(distanceDurationDTO.getDistanceKm())
                    .carbonCost(carbonEmission)
                    .estimatedTime(distanceDurationDTO.getDurationInMins())
                    .isPooledEligible(willingToPool)
                    .carbonEmission(carbonEmission)
                    .build();
            return routeRepository.save(newRoute);
        });

        // Create trip
        Trip trip = new Trip(riderId, best.getDriverId().toString(), best.getDriverName(), route, LocalDateTime.now());
        tripRepository.save(trip);

        return driverMapper.toDto(best);
    }
}
