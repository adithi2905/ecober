package com.ecober.domain.service;

import com.ecober.domain.model.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverScoringServiceTest {

    private GeocodingService geocodingService;
    private DriverScoringService driverScoringService;

    @BeforeEach
    void setUp() {
        geocodingService = mock(GeocodingService.class);
        driverScoringService = new DriverScoringService();
        inject(driverScoringService, "geocodingService", geocodingService);
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
    void testRankDrivers_HigherScoreRanksFirst() {
        // Arrange
        Driver driver1 = new Driver(); // Closer, high trust, low CO2
        driver1.setDriverLocation("A");
        driver1.setTrustScore(4.5);
        driver1.setTotalCO2Saved(80);

        Driver driver2 = new Driver(); // Farther, low trust, high CO2
        driver2.setDriverLocation("B");
        driver2.setTrustScore(2.0);
        driver2.setTotalCO2Saved(95);

        when(geocodingService.getLatAndLong("A")).thenReturn(new double[]{10.0, 10.0});
        when(geocodingService.getLatAndLong("B")).thenReturn(new double[]{20.0, 20.0});

        double pickupLat = 10.5;
        double pickupLng = 10.5;

        // Act
        List<Driver> ranked = driverScoringService.rankDrivers(List.of(driver2, driver1), pickupLat, pickupLng);

        // Assert
        assertEquals(driver1, ranked.get(0)); // driver1 should rank higher due to proximity + trust
        assertEquals(driver2, ranked.get(1));
    }

    @Test
    void testNormalizeTrustScore() {
        double maxScore = invokeNormalizeTrustScore(5.0);
        double midScore = invokeNormalizeTrustScore(2.5);
        double overMax = invokeNormalizeTrustScore(6.0);

        assertEquals(1.0, maxScore, 0.001);
        assertEquals(0.5, midScore, 0.001);
        assertEquals(1.0, overMax, 0.001);
    }

    @Test
    void testNormalizeCO2Saved() {
        double capped = invokeNormalizeCO2Saved(120.0);
        double mid = invokeNormalizeCO2Saved(50.0);
        double low = invokeNormalizeCO2Saved(0.0);

        assertEquals(1.0, capped, 0.001);
        assertEquals(0.5, mid, 0.001);
        assertEquals(0.0, low, 0.001);
    }

    // Reflection-based internal method testing helpers
    private double invokeNormalizeTrustScore(double val) {
        try {
            var method = DriverScoringService.class.getDeclaredMethod("normalizeTrustScore", double.class);
            method.setAccessible(true);
            return (double) method.invoke(driverScoringService, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double invokeNormalizeCO2Saved(double val) {
        try {
            var method = DriverScoringService.class.getDeclaredMethod("normalizeCO2Saved", double.class);
            method.setAccessible(true);
            return (double) method.invoke(driverScoringService, val);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
