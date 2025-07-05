package com.ecober.domain.service;

import com.ecober.util.EmissionUtils;
import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarbonScoringService {

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private EmissionUtils emissionUtils;

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
            default        -> emissionFactor = 0.21;
        }

        return distanceKm * (baselineFactor - emissionFactor);
    }

    public double calculateTripDistanceInKm(String pickupLocation, String dropoffLocation) {
        double[] pickup = geocodingService.getLatAndLong(pickupLocation);
        double[] dropoff = geocodingService.getLatAndLong(dropoffLocation);
        return GeoUtils.haversinDistance(pickup[0], pickup[1], dropoff[0], dropoff[1]);
    }

    public double computeScoreWithDistanceAndEmissions(String pickupLocation, String dropoffLocation, String vehicleType, double d) {
        double distanceKm = calculateTripDistanceInKm(pickupLocation, dropoffLocation);
        double expectedEmission = emissionUtils.Co2EstimatedEmission(distanceKm, vehicleType);
        double actualEmissions = emissionUtils.Co2ActualEmission(distanceKm, vehicleType);

        double efficiency = (expectedEmission == 0) ? 1.0 : Math.min(1.0, expectedEmission / actualEmissions);
        double rawScore = efficiency * 100;

        return Math.min(100.0, Math.max(0.0, rawScore));
    }

    public double computeActualCO2Emission(String pickupLocation, String dropoffLocation, String vehicleType) {
        double distanceKm = calculateTripDistanceInKm(pickupLocation, dropoffLocation);
        return emissionUtils.Co2ActualEmission(distanceKm, vehicleType);
    }

    public double computeEstimatedCO2Emission(String pickupLocation, String dropoffLocation, String vehicleType) {
        double distanceKm = calculateTripDistanceInKm(pickupLocation, dropoffLocation);
        return emissionUtils.Co2EstimatedEmission(distanceKm, vehicleType);
    }

    public double calculateCarbonCost(double actualCO2Kg) {
        final double CARBON_PRICE_PER_TON = 100.0; // USD
        return actualCO2Kg * (CARBON_PRICE_PER_TON / 1000.0);
    }
}
