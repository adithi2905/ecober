package com.ecober.util;

public class GeoUtils {

    public static double calculateEmissions(double distanceKm,String vehicleType)
    {
        double factor=switch(vehicleType.toUpperCase())
        {
            case "EV" ->0.05;
            case "BIKE" ->0.08;
            case "SUV" ->0.25;
            case "SEDAN" ->0.21;
            default ->0.21;
        };
        return distanceKm*factor;
    }

    public static double haversinDistance(double lat1,double lon1,double lat2,double lon2)
    {
        final int EARTH_RADIUS=6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(EARTH_RADIUS * c * 100.0) / 100.0;

    }
    
}
