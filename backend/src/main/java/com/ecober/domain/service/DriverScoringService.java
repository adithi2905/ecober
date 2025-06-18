package com.ecober.domain.service;

import com.ecober.domain.model.Driver;
import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DriverScoringService {

    @Autowired
    private GeocodingService geocodingService;

    public List<Driver> rankDrivers(List<Driver> drivers, double pickupLat, double pickupLng) {
        return drivers.stream()
                .sorted(Comparator.comparingDouble(driver ->
                        -scoreDriver(driver, pickupLat, pickupLng) // Higher score = better
                ))
                .toList();
    }

    private double scoreDriver(Driver driver, double pickupLat, double pickupLng) {
        double[] driverCoords = geocodingService.getLatAndLong(driver.getDriverLocation());
        double distanceKm = GeoUtils.haversinDistance(pickupLat, pickupLng, driverCoords[0], driverCoords[1]);
        double distanceScore = 1 / (1 + distanceKm); // closer = higher score

        double trustScore = normalizeTrustScore(driver.getTrustScore());       // assume 0–5 scale
        double co2SavedScore = normalizeCO2Saved(driver.getTotalCO2Saved());   // assume 0–100+ kg

        return 0.5 * distanceScore + 0.3 * trustScore + 0.2 * co2SavedScore;
    }

    private double normalizeTrustScore(double score) {
        return Math.min(1.0, score / 5.0);
    }

    private double normalizeCO2Saved(double co2Saved) {
        return Math.min(1.0, co2Saved / 100.0);
    }
}
