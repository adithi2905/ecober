package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.Route;
import com.ecober.infrastructure.repository.RouteRepository;
import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    public Route getOrCreateRoute(Location pickup, Location dropoff, String vehicleType) {
        Optional<Route> existing = routeRepository.findByCoordinates(
                pickup.getLatitude(), pickup.getLongitude(),
                dropoff.getLatitude(), dropoff.getLongitude());

        if (existing.isPresent()) return existing.get();

        DistanceDurationDTO distanceDuration;
        try {
            distanceDuration = routeOptimizingService.getDistanceAndETA(pickup, dropoff);
        } catch (Exception e) {
            distanceDuration = GeoUtils.haversinDistanceandDuration(
                    pickup.getLatitude(), pickup.getLongitude(),
                    dropoff.getLatitude(), dropoff.getLongitude());
        }

        double emission = GeoUtils.calculateEmissions(distanceDuration.getDistanceKm(), vehicleType);

        Route route = Route.builder()
                .routeID(UUID.randomUUID().toString())
                .source(pickup)
                .destination(dropoff)
                .distanceKm(distanceDuration.getDistanceKm())
                .carbonCost(emission)
                .estimatedTime(distanceDuration.getDurationInMins())
                .build();

        return routeRepository.save(route);
    }
}
