package com.ecober.domain.service;

import org.springframework.stereotype.Service;

@Service
public class CarbonScoringService {
    
    private static final double EXCELLENT_THRESHOLD = 10.0;
    private static final double GOOD_THRESHOLD = 25.0;
    private static final double AVERAGE_THRESHOLD = 50.0;
    
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
        return switch(vehicleType.toUpperCase()) {
            case "EV" -> 0.05;
            case "BIKE" -> 0.08;
            case "SUV" -> 0.25;
            case "SEDAN" -> 0.21;
            default -> 0.21;
        };
    }
}

    
