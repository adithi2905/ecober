package com.ecober.domain.service;

import org.springframework.stereotype.Service;

@Service
public class CarbonScoringService {

    private static final double EMISSION_THRESHOLD_LOW = 0.1;  
    private static final double EMISSION_THRESHOLD_HIGH = 0.2;

    public String getEcoScore(double carbonEmitted) {
        if (carbonEmitted < EMISSION_THRESHOLD_LOW) {
            return "A+ (Excellent)";
        } else if (carbonEmitted < EMISSION_THRESHOLD_HIGH) {
            return "B (Good)";
        } else {
            return "C (Needs Improvement)";
        }
    }

    public double calculateEmissionSavings(double distanceKm, String vehicleType) {
        double petrolEmissionPerKm = 0.21;
        double electricEmissionPerKm = 0.05;

        double baseEmission = petrolEmissionPerKm * distanceKm;
        double currentEmission = (vehicleType.equalsIgnoreCase("Electric") ? electricEmissionPerKm : petrolEmissionPerKm) * distanceKm;

        return baseEmission - currentEmission; 
    }

    public String getCarbonFeedback(double savingsKg) {
        if (savingsKg >= 20) return "You're a Climate Hero 🌱";
        else if (savingsKg >= 5) return "Nice! You're helping the planet 🌍";
        else return "Every small step counts 🚲";
    }
}
