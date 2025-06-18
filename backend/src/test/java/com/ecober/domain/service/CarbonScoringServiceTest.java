package com.ecober.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarbonScoringServiceTest {

    private GeocodingService geocodingService;
    private CarbonScoringService carbonScoringService;

    @BeforeEach
    void setUp() throws Exception {
        geocodingService = mock(GeocodingService.class);
        carbonScoringService = new CarbonScoringService();

        // Inject mock using reflection
        Field field = CarbonScoringService.class.getDeclaredField("geocodingService");
        field.setAccessible(true);
        field.set(carbonScoringService, geocodingService);
    }

    @Test
    void testCalculateCarbonScore() {
        assertEquals(100.0, carbonScoringService.calculateCarbonScore(0.0, 0), 0.001);
        double score = carbonScoringService.calculateCarbonScore(20.0, 2);
        assertTrue(score < 100.0 && score > 0.0);
    }

    @Test
    void testGetCarbonRating() {
        assertEquals("A+", carbonScoringService.getCarbonRating(95));
        assertEquals("C", carbonScoringService.getCarbonRating(65)); // Corrected expected value
        assertEquals("F", carbonScoringService.getCarbonRating(25));
    }

    @Test
    void testCalculateCO2Savings() {
        double savings = carbonScoringService.calculateCO2Savings(2.0, "EV");
        assertTrue(savings > 0);
    }

    @Test
    void testCalculateTripDistanceInKm() {
        when(geocodingService.getLatAndLong("A")).thenReturn(new double[]{12.9716, 77.5946});
        when(geocodingService.getLatAndLong("B")).thenReturn(new double[]{13.0827, 80.2707});

        double distance = carbonScoringService.calculateTripDistanceInKm("A", "B");
        assertTrue(distance > 0);
    }

    @Test
    void testComputeScoreWithDistanceAndEmissions() {
        when(geocodingService.getLatAndLong("A")).thenReturn(new double[]{12.9716, 77.5946});
        when(geocodingService.getLatAndLong("B")).thenReturn(new double[]{13.0827, 80.2707});

        double score = carbonScoringService.computeScoreWithDistanceAndEmissions("A", "B", "SEDAN", 5.0);
        assertTrue(score >= 0 && score <= 100);
    }

    @Test
    void testGetEmissionFactor_Default() {
        double savings = carbonScoringService.calculateCO2Savings(10.0, "UNKNOWN");
        assertEquals(7.9, savings, 0.001); // 10 * (1 - 0.21)
    }
}
