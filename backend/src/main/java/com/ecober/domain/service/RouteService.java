package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.Route;
import com.ecober.util.EmissionUtils;
import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RouteService {

   
    @Autowired
    private RouteOptimizingService routeOptimizingService;

    @Autowired
    private EmissionUtils emissionUtils;

    @Autowired
    private CarbonScoringService carbonScoringService;

public Route getOrCreateRoute(Location pickup, Location dropoff, String vehicleType) {
    DistanceDurationDTO distanceDuration;
    try {
        distanceDuration = routeOptimizingService.getDistanceAndETA(pickup, dropoff);
    } catch (Exception e) {
        distanceDuration = GeoUtils.calculateDistanceAndDuration(
                pickup.getLatitude(), pickup.getLongitude(),
                dropoff.getLatitude(), dropoff.getLongitude());
    }

    double emission = emissionUtils.Co2ActualEmission(distanceDuration.getDistanceKm(), vehicleType);
    double carbonCost=carbonScoringService.calculateCarbonCost(emission);

    return Route.builder()
            .source(pickup)
            .destination(dropoff)
            .distanceKm(distanceDuration.getDistanceKm())
            .carbonCost(carbonCost)
            .estimatedTime(distanceDuration.getDurationInMins())
            .estimatedEmission(emission)
            .isPooledEligible(false)
            .build();
}
}
