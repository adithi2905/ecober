package com.ecober.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;

public class Co2AnalyticsServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private Co2AnalyticsService co2AnalyticsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); 
    }

    private Trip createTrip(double emissions) {
        Trip trip = new Trip();
        trip.setEstimatedEmission(emissions); 
        return trip;
    }

    @Test
    public void testGetRiderCarbonEmission_GreenRider() {
        UUID riderID = UUID.randomUUID();
        List<Trip> mockTrips = Arrays.asList(
            createTrip(5.0),
            createTrip(10.0),
            createTrip(3.0)
        );
        when(tripRepository.findByUser_UserId(riderID)).thenReturn(mockTrips);
        CarbonDTO result = co2AnalyticsService.getRiderCarbonEmission(riderID);

        assertEquals("🌿 Green Rider", result.getEcoBadge()); 
        assertEquals(18.0, result.getTotalEmissions());
        assertEquals(3, result.getTotalTrips());
        assertEquals(6.0, result.getAverageEmissionPerTrip());
    }

    @Test
    public void testGetRiderCarbonEmission_FrequentRider() {
        UUID riderId = UUID.randomUUID();
        List<Trip> mockTrips = Arrays.asList(
            createTrip(30.0),
            createTrip(40.0)
        );
        when(tripRepository.findByUser_UserId(riderId)).thenReturn(mockTrips);
        CarbonDTO result = co2AnalyticsService.getRiderCarbonEmission(riderId);

        assertEquals("🚗 Frequent Rider", result.getEcoBadge()); 
    }
}
