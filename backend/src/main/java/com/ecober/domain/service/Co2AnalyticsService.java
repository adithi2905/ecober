package com.ecober.domain.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;

@Service
public class Co2AnalyticsService
{
    @Autowired
    TripRepository tripRepository;

    private String determineBadge(double totalCarbonEmission)
    {
        if(totalCarbonEmission<20)
        return "🌿 Green Rider";
        else if(totalCarbonEmission<50)
        return "🌤 Conscious Rider";
        else
        return "🚗 Frequent Rider";
    }

    public CarbonDTO getRiderCarbonEmission(String riderID)
    {
        List<Trip>trips=tripRepository.findByUserId(riderID);
        int totalTrips=trips.size();
        double totalEmissions=0.0;
        for(int i=0;i<trips.size();i++)
        {
            totalEmissions+=trips.get(i).getCarbonEmissions();
        }
        String badge=determineBadge(totalEmissions);
        double averageEmissionPerTrip=totalEmissions/totalTrips;
        return CarbonDTO.builder().riderId(riderID).totalEmissions(totalEmissions).totalTrips(totalTrips).averageEmissionPerTrip(averageEmissionPerTrip).ecoBadge(badge).build();     
    }
}