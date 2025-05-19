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

    CarbonDTO carbonDto;

    public CarbonDTO getRiderCarbonEmission(String riderID)
    {
        List<Trip>trips=tripRepository.findByUserId(riderID);
        int totalTrips=trips.size();
        double totalEmissions=0.0;
        for(int i=0;i<trips.size();i++)
        {
            totalEmissions+=trips.get(i).getCarbonEmissions();
        }
        double averageEmissionPerTrip=totalEmissions/totalTrips;
        carbonDto=CarbonDTO.builder().riderId(riderID).totalEmissions(totalEmissions).totalTrips(totalTrips).averageEmissionPerTrip(averageEmissionPerTrip).ecoBadge("friendly").build();     
        return carbonDto;
    }
}