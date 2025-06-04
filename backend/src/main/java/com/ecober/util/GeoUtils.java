package com.ecober.util;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.domain.model.Location;

public class GeoUtils {

    private static final int EARTH_RADIUS_KM = 6371;

    // Haversine formula
    public static double haversinDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    // Fallback if external APIs fail
    public static DistanceDurationDTO haversinDistanceandDuration(double lat1, double lon1, double lat2, double lon2) {
        double distanceKm = haversinDistance(lat1, lon1, lat2, lon2);
        int durationMins = (int) (distanceKm / 50.0 * 60); // Assuming avg 50 km/h
        return new DistanceDurationDTO(distanceKm, durationMins, durationMins * 60);

    }

    // Overloaded for Location objects
    public static double distanceBetween(Location a,Location b) {
        return haversinDistance(a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude());
    }

    // CO2 Emission calculator
    public static double calculateEmissions(double distanceKm, String vehicleType) {
        // Emission rates in kg CO2/km
        return switch (vehicleType.toUpperCase()) {
            case "EV" -> distanceKm * 0.18;
            case "HYBRID" -> distanceKm * 0.104;
            case "SEDAN" -> distanceKm * 0.173;
            case "SUV" -> distanceKm * 0.231;
            case "BIKE" -> 0.0;
            default -> distanceKm * 0.18; // fallback
        };
    }
}
