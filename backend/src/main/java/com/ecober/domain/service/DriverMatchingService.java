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
public class DriverMatchingService
{
    @Autowired
    DriverRepository driverRepository;

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

        // Step 1: Construct pickup and dropoff locations
        Location pickup = new Location(pickupLatitude, pickupLongitude, riderPickupAddress, 0.0);
        Location dropoff = new Location(dropoffLatitude, dropoffLongitude, riderDropoffAddress, 0.0);

        // Step 2: Estimate distance and duration
        DistanceDurationDTO distanceDTO;
        try {
            distanceDTO = routeOptimizingService.getDistanceAndETA(pickup, dropoff);
        } catch (Exception ex) {
            distanceDTO = GeoUtils.haversinDistanceandDuration(
                    pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude
            );
        }

        double carbonEmission = GeoUtils.calculateEmissions(distanceDTO.getDistanceKm(), preferredVehicleType);

        // Step 3: Reuse or create a route
        Route route = routeRepository.findByCoordinates(
                pickup.getLatitude(), pickup.getLongitude(),
                dropoff.getLatitude(), dropoff.getLongitude()
        ).orElse(null);

        if (route == null) {
            route = Route.builder()
                    .routeID(UUID.randomUUID().toString())
                    .source(pickup)
                    .destination(dropoff)
                    .distanceKm(distanceDTO.getDistanceKm())
                    .carbonCost(carbonEmission)
                    .estimatedTime(distanceDTO.getDurationInMins())
                    .isPooledEligible(willingToPool)
                    .carbonEmission(carbonEmission)
                    .build();
            route = routeRepository.save(route);
        }

        // Step 4: Find nearest driver using Haversine distance
        List<Driver> drivers = driverRepository.findAll(); // You can optimize this later
        Driver best = drivers.stream()
                .min((d1, d2) -> {
                    Location loc1 = parseDriverLocation(d1.getDriverLocation());
                    Location loc2 = parseDriverLocation(d2.getDriverLocation());
                    double d1Dist = GeoUtils.distanceBetween(pickup, loc1);
                    double d2Dist = GeoUtils.distanceBetween(pickup, loc2);
                    return Double.compare(d1Dist, d2Dist);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable driver found"));

        // Step 5: Create trip and persist
        Trip trip = new Trip(riderId, best.getDriverId().toString(), best.getDriverName(), route, LocalDateTime.now());
        tripRepository.save(trip);

        return driverMapper.toDto(best);
    }

    private Location parseDriverLocation(String driverLocStr) {
        try {
            String[] parts = driverLocStr.split(",");
            return new Location(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), null, 0.0);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid driverLocation format: " + driverLocStr);
        }
    }
}
