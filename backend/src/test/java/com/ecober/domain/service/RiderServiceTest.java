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

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.TripMapper;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.TripRepository;

public class RiderServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private RiderService riderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Trip createTrip(double emissions) {
        Trip trip = new Trip();
        trip.setCarbonEmissions(emissions);
        return trip;
    }

    private TripDTO createTripDTO(double emissions) {
        TripDTO dto = new TripDTO();
        dto.setCarbonEmissions(emissions);
        return dto;
    }

    @Test
    public void testFetchAllTrips() {
        UUID riderId = UUID.randomUUID();
        List<Trip> trips = Arrays.asList(createTrip(5), createTrip(10), createTrip(13));
        List<TripDTO> tripDTOs = Arrays.asList(createTripDTO(5), createTripDTO(10), createTripDTO(13));

        when(tripRepository.findByUserId(riderId)).thenReturn(trips);
        when(tripMapper.toDtoList(trips)).thenReturn(tripDTOs);

        List<TripDTO> result = riderService.fetchAllTrips(riderId.toString());

        assertEquals(3, result.size());
        assertEquals(10.0, result.get(1).getCarbonEmissions());
    }
}
