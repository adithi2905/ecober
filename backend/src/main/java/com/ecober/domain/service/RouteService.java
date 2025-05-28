package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
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

    public Route getOrCreateRoute(String pickup, String dropoff,
                                  double pickupLat, double pickupLng,
                                  double dropoffLat, double dropoffLng,
                                  String vehicleType) {

        Optional<Route> existing = routeRepository.findBySourceAndDestination(pickup, dropoff);
        if (existing.isPresent()) return existing.get();

        DistanceDurationDTO distanceDuration;
        try {
            distanceDuration = routeOptimizingService.getDistanceAndETA(pickupLat, pickupLng, dropoffLat, dropoffLng);
        } catch (Exception e) {
            distanceDuration = GeoUtils.haversinDistanceandDuration(pickupLat, pickupLng, dropoffLat, dropoffLng);
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
