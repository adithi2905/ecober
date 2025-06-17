package com.ecober.domain.service;

import com.ecober.adapter.Dto.CarbonDTO;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Co2AnalyticsServiceTest {

    private TripRepository tripRepository;
    private Co2AnalyticsService co2AnalyticsService;

    @BeforeEach
    void setUp() {
        tripRepository = mock(TripRepository.class);
        co2AnalyticsService = new Co2AnalyticsService();
        inject(co2AnalyticsService, "tripRepository", tripRepository);
    }

    private void inject(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetRiderCarbonEmission_EcoChampion() {
        UUID riderId = UUID.randomUUID();
        Trip trip1 = new Trip(); trip1.setEstimatedEmission(6.0);
        Trip trip2 = new Trip(); trip2.setEstimatedEmission(8.0);

        when(tripRepository.findByUser_UserId(riderId)).thenReturn(List.of(trip1, trip2));

        CarbonDTO dto = co2AnalyticsService.getRiderCarbonEmission(riderId);

        assertEquals(riderId, dto.getRiderId());
        assertEquals(2, dto.getTotalTrips());
        assertEquals(14.0, dto.getTotalEmissions(), 0.001);
        assertEquals(7.0, dto.getAverageEmissionPerTrip(), 0.001);
        assertEquals("♻ Eco Champion", dto.getEcoBadge());
    }

    @Test
    void testGetRiderCarbonEmission_SustainableCommuter() {
        UUID riderId = UUID.randomUUID();
        Trip trip1 = new Trip(); trip1.setEstimatedEmission(20.0);
        Trip trip2 = new Trip(); trip2.setEstimatedEmission(25.0);

        when(tripRepository.findByUser_UserId(riderId)).thenReturn(List.of(trip1, trip2));

        CarbonDTO dto = co2AnalyticsService.getRiderCarbonEmission(riderId);

        assertEquals("⚖ Sustainable Commuter", dto.getEcoBadge());
    }

    @Test
    void testGetRiderCarbonEmission_ActiveExplorer() {
        UUID riderId = UUID.randomUUID();
        Trip trip1 = new Trip(); trip1.setEstimatedEmission(35.0);
        Trip trip2 = new Trip(); trip2.setEstimatedEmission(30.0);

        when(tripRepository.findByUser_UserId(riderId)).thenReturn(List.of(trip1, trip2));

        CarbonDTO dto = co2AnalyticsService.getRiderCarbonEmission(riderId);

        assertEquals("✈ Active Explorer", dto.getEcoBadge());
    }

    @Test
    void testGetRiderCarbonEmission_NoTrips() {
        UUID riderId = UUID.randomUUID();

        when(tripRepository.findByUser_UserId(riderId)).thenReturn(List.of());

        CarbonDTO dto = co2AnalyticsService.getRiderCarbonEmission(riderId);

        assertEquals(0.0, dto.getTotalEmissions(), 0.001);
        assertEquals(0, dto.getTotalTrips());
        assertEquals(0.0, dto.getAverageEmissionPerTrip(), 0.001);
        assertEquals("♻ Eco Champion", dto.getEcoBadge()); // default since 0 < 20
    }
}
