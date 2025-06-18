package com.ecober.domain.service;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.TripperMapper;
import com.ecober.domain.model.*;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripperMapper tripperMapper;

    @InjectMocks
    private TripService tripService;

    private UUID riderId;
    private UUID driverId;
    private Trip mockTrip;
    private Driver mockDriver;
    private User mockUser;

    @BeforeEach
    void setUp() {
        riderId = UUID.randomUUID();
        driverId = UUID.randomUUID();
        mockUser = User.builder().userId(riderId).username("rider@example.com").build();
        mockDriver = Driver.builder().driverId(driverId).email("driver@example.com").build();

        Location pickup = new Location(12.9352, 77.6146, "Koramangala", 920.0);
        Location drop = new Location(12.8452, 77.6646, "Electronic City", 940.0);

        Route route = Route.builder()
                .source(pickup)
                .destination(drop)
                .distanceKm(12.0)
                .carbonCost(1.2)
                .estimatedTime(25.0)
                .isPooledEligible(false)
                .carbonEmission(1.5)
                .build();

        mockTrip = Trip.builder()
                .tripId(UUID.randomUUID())
                .user(mockUser)
                .driver(mockDriver)
                .route(route)
                .estimatedEmission(10.0)
                .status(TripStatus.ACCEPTED)
                .ecoScore("B+")
                .build();
    }

    @Test
    void testCreateTrip() {
        when(userRepository.findById(riderId)).thenReturn(Optional.of(mockUser));

        tripService.createTrip(riderId, mockDriver, mockTrip.getRoute(), mockTrip.getEstimatedEmission());

        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    void testFetchAllTrips() {
        List<Trip> tripList = List.of(mockTrip);
        TripDTO tripDTO = new TripDTO();

        when(tripRepository.findByUser_UserId(riderId)).thenReturn(tripList);
        when(tripperMapper.toDtoList(tripList)).thenReturn(List.of(tripDTO));

        List<TripDTO> result = tripService.fetchAllTrips(riderId);
        assertThat(result).hasSize(1);
    }

    @Test
    void testFetchAllDriverTrips() {
        List<Trip> tripList = List.of(mockTrip);
        TripDTO tripDTO = new TripDTO();

        when(tripRepository.findByDriver_DriverIdAndStatus(driverId, TripStatus.COMPLETED)).thenReturn(tripList);
        when(tripperMapper.toDtoList(tripList)).thenReturn(List.of(tripDTO));

        List<TripDTO> result = tripService.fetchAllDriverTrips(driverId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(tripDTO);
    }

    @Test
    void testStartTrip_success() {
        when(tripRepository.findAcceptedRide(driverId, TripStatus.ACCEPTED)).thenReturn(mockTrip);

        boolean started = tripService.startTrip(driverId);

        assertThat(started).isTrue();
        verify(tripRepository).save(mockTrip);
        assertThat(mockTrip.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
    }

    @Test
    void testStartTrip_failIfNotFound() {
        when(tripRepository.findAcceptedRide(driverId, TripStatus.ACCEPTED)).thenReturn(null);
        boolean result = tripService.startTrip(driverId);
        assertThat(result).isFalse();
    }

    @Test
    void testEndTrip_success() {
        mockTrip.setStatus(TripStatus.IN_PROGRESS);
        when(tripRepository.findByTripId(mockTrip.getTripId())).thenReturn(mockTrip);

        boolean ended = tripService.endTrip(mockTrip.getTripId(), driverId);

        assertThat(ended).isTrue();
        assertThat(mockTrip.getStatus()).isEqualTo(TripStatus.COMPLETED);
        verify(tripRepository).save(mockTrip);
    }

    @Test
    void testEndTrip_failIfStatusWrong() {
        mockTrip.setStatus(TripStatus.ACCEPTED);
        when(tripRepository.findByTripId(mockTrip.getTripId())).thenReturn(mockTrip);

        boolean ended = tripService.endTrip(mockTrip.getTripId(), driverId);

        assertThat(ended).isFalse();
    }

    @Test
    void testGetTripById_returnsDTO() {
        when(tripRepository.findByTripId(mockTrip.getTripId())).thenReturn(mockTrip);
        TripDTO tripDTO = new TripDTO();
        when(tripperMapper.toDto(mockTrip)).thenReturn(tripDTO);

        Optional<TripDTO> result = tripService.getTripById(mockTrip.getTripId());

        assertThat(result).isPresent();
    }

    @Test
    void testFetchCurrentTrip_returnsDTO() {
        when(tripRepository.findCurrentTrip(riderId)).thenReturn(mockTrip);
        TripDTO tripDTO = new TripDTO();
        when(tripperMapper.toDto(mockTrip)).thenReturn(tripDTO);

        TripDTO result = tripService.fetchCurrentTrip(riderId);

        assertThat(result).isEqualTo(tripDTO);
    }
}
