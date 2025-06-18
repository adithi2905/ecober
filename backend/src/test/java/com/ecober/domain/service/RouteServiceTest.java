package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

    @Mock
    private RouteOptimizingService routeOptimizingService;

    @InjectMocks
    private RouteService routeService;

    private Location pickup;
    private Location dropoff;

    @BeforeEach
    void setUp() {
        pickup = new Location(12.9352, 77.6146, "Koramangala", 0);
        dropoff = new Location(12.8452, 77.6600, "Electronic City", 0);
    }

    @Test
    void testGetOrCreateRoute_successfulFromAPI() throws Exception {
        DistanceDurationDTO dto = new DistanceDurationDTO(15.0, 20, 1200);
        when(routeOptimizingService.getDistanceAndETA(pickup, dropoff)).thenReturn(dto);

        Route result = routeService.getOrCreateRoute(pickup, dropoff, "SEDAN");

        assertThat(result).isNotNull();
        assertThat(result.getDistanceKm()).isEqualTo(15.0);
        assertThat(result.getEstimatedTime()).isEqualTo(20);
        assertThat(result.getCarbonEmission()).isGreaterThan(0);
        assertThat(result.getSource()).isEqualTo(pickup);
        assertThat(result.getDestination()).isEqualTo(dropoff);
    }

    @Test
    void testGetOrCreateRoute_fallbackToHaversin() throws Exception {
        when(routeOptimizingService.getDistanceAndETA(pickup, dropoff))
                .thenThrow(new RuntimeException("API Error"));

        Route result = routeService.getOrCreateRoute(pickup, dropoff, "SUV");

        assertThat(result).isNotNull();
        assertThat(result.getDistanceKm()).isGreaterThan(0);
        assertThat(result.getCarbonEmission()).isGreaterThan(0);
        assertThat(result.getEstimatedTime()).isGreaterThan(0);
    }
}
