package com.ecober.domain.service;

import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarbonScoringService {

    private static final double EXCELLENT_THRESHOLD = 10.0;
    private static final double GOOD_THRESHOLD = 25.0;
    private static final double AVERAGE_THRESHOLD = 50.0;

    @Autowired
    private GeocodingService geocodingService;

    public double calculateCarbonScore(double totalEmissions, int totalTrips) {
        if (totalTrips == 0) return 100.0;

        double averageEmissionPerTrip = totalEmissions / totalTrips;

        if (averageEmissionPerTrip <= EXCELLENT_THRESHOLD) {
            return 100.0 - (averageEmissionPerTrip / EXCELLENT_THRESHOLD * 10);
        } else if (averageEmissionPerTrip <= GOOD_THRESHOLD) {
            return 90.0 - ((averageEmissionPerTrip - EXCELLENT_THRESHOLD) / (GOOD_THRESHOLD - EXCELLENT_THRESHOLD) * 20);
        } else if (averageEmissionPerTrip <= AVERAGE_THRESHOLD) {
            return 70.0 - ((averageEmissionPerTrip - GOOD_THRESHOLD) / (AVERAGE_THRESHOLD - GOOD_THRESHOLD) * 30);
        } else {
            return Math.max(0, 40.0 - ((averageEmissionPerTrip - AVERAGE_THRESHOLD) / 50.0 * 40));
        }
    }

    public String getCarbonRating(double score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 50) return "C+";
        if (score >= 40) return "C";
        if (score >= 30) return "D";
        return "F";
    }

    public double calculateCO2Savings(double actualEmissions, String vehicleType) {
        double averageCarEmission = actualEmissions * (0.21 / getEmissionFactor(vehicleType));
        return Math.max(0, averageCarEmission - actualEmissions);
    }

    private double getEmissionFactor(String vehicleType) {
        return switch (vehicleType.toUpperCase()) {
            case "EV" -> 0.05;
            case "BIKE" -> 0.08;
            case "SUV" -> 0.25;
            case "SEDAN" -> 0.21;
            default -> 0.21;
        };
    }

    public double calculateTripDistanceInKm(String pickupLocation, String dropoffLocation) {
        double[] start = geocodingService.getLatAndLong(pickupLocation);
        double[] end = geocodingService.getLatAndLong(dropoffLocation);
        return GeoUtils.calculateDistanceAndDuration(start[0], start[1], end[0], end[1]).getDistanceKm();
    }

    public double computeScoreWithDistanceAndEmissions(String pickup, String drop, String vehicleType, double actualEmissions) {
        double tripKm = calculateTripDistanceInKm(pickup, drop);
        double expectedEmission = tripKm * getEmissionFactor(vehicleType);
        double savings = Math.max(0, expectedEmission - actualEmissions);
        double scoreBoost = (savings / expectedEmission) * 20.0;
        return Math.min(100.0, 70.0 + scoreBoost); // boost a base score of 70
    }
}
