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
            return 40.0; // very poor score
        }
    }

    public String getCarbonRating(double score) {
        if (score >= 90) return "A+";
        else if (score >= 80) return "A";
        else if (score >= 70) return "B";
        else if (score >= 60) return "C";
        else if (score >= 50) return "D";
        else return "F";
    }

    public double calculateCO2Savings(double distanceKm, String vehicleType) {
        double baselineFactor = 1.0;
        double emissionFactor;

        switch (vehicleType.toUpperCase()) {
            case "EV"     -> emissionFactor = 0.18;
            case "HYBRID" -> emissionFactor = 0.104;
            case "SEDAN"  -> emissionFactor = 0.173;
            case "SUV"    -> emissionFactor = 0.231;
            case "BIKE"   -> emissionFactor = 0.0;
            default       -> emissionFactor = 0.21; 
        }

        return distanceKm * (baselineFactor - emissionFactor);
    }

    public double calculateTripDistanceInKm(String pickupLocation, String dropoffLocation) {
        double[] pickup = geocodingService.getLatAndLong(pickupLocation);
        double[] dropoff = geocodingService.getLatAndLong(dropoffLocation);
        return GeoUtils.haversinDistance(pickup[0], pickup[1], dropoff[0], dropoff[1]);
    }

    public double computeScoreWithDistanceAndEmissions(String pickupLocation, String dropoffLocation, String vehicleType, double actualEmissions) {
        double distanceKm = calculateTripDistanceInKm(pickupLocation, dropoffLocation);
        double expectedEmission = GeoUtils.calculateEmissions(distanceKm, vehicleType);

        double efficiency = (expectedEmission == 0) ? 1.0 : Math.min(1.0, expectedEmission / actualEmissions);
        double rawScore = efficiency * 100;

        return Math.min(100.0, Math.max(0.0, rawScore));
    }
}
