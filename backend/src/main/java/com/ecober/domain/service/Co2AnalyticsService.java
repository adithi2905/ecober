package com.ecober.domain.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;

@Service
public class Co2AnalyticsService {

    @Autowired
    TripRepository tripRepository;

    private String determineBadge(double totalCarbonEmission) {
        if (totalCarbonEmission < 20)
            return "♻ Eco Champion";             
        else if (totalCarbonEmission < 50)
            return "⚖ Sustainable Commuter";     
        else
            return "✈ Active Explorer";          
    }

    public CarbonDTO getRiderCarbonEmission(UUID riderID) {
        List<Trip> trips = tripRepository.findByUser_UserId(riderID);
        int totalTrips = trips.size();
        double totalEmissions = 0.0;

        for (Trip trip : trips) {
            totalEmissions += trip.getEstimatedEmission();
        }

        String badge = determineBadge(totalEmissions);
        double averageEmissionPerTrip = totalTrips == 0 ? 0 : totalEmissions / totalTrips;

        return CarbonDTO.builder()
                .riderId(riderID)
                .totalEmissions(totalEmissions)
                .totalTrips(totalTrips)
                .averageEmissionPerTrip(averageEmissionPerTrip)
                .ecoBadge(badge)
                .build();
    }
}
