package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.Route;
import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

   
    @Autowired
    private RouteOptimizingService routeOptimizingService;

public Route getOrCreateRoute(Location pickup, Location dropoff, String vehicleType) {
    DistanceDurationDTO distanceDuration;
    try {
        distanceDuration = routeOptimizingService.getDistanceAndETA(pickup, dropoff);
    } catch (Exception e) {
        distanceDuration = GeoUtils.haversinDistanceandDuration(
                pickup.getLatitude(), pickup.getLongitude(),
                dropoff.getLatitude(), dropoff.getLongitude());
    }

    double emission = GeoUtils.calculateEmissions(distanceDuration.getDistanceKm(), vehicleType);

    return Route.builder()
            .source(pickup)
            .destination(dropoff)
            .distanceKm(distanceDuration.getDistanceKm())
            .carbonCost(emission)
            .estimatedTime(distanceDuration.getDurationInMins())
            .carbonEmission(emission)
            .isPooledEligible(false)
            .build();
}
}
