package com.ecober.domain.service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.User;
import com.ecober.infrastructure.repository.RideRequestRepository;
import com.ecober.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideRequestServiceTest {

    @Mock
    private RideRequestRepository rideRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private BroadcastingService broadcastingService;

    @InjectMocks
    private RideRequestService rideRequestService;

    private UUID userId;
    private User mockUser;
    private RideRequestDTO requestDTO;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        mockUser = User.builder().userId(userId).username("Adithi").build();
        requestDTO = RideRequestDTO.builder()
                .pickupLocation("Koramangala")
                .dropoffLocation("Electronic City")
                .preferredVehicleType("Sedan")
                .willingToPool(false)
                .build();
    }

    @Test
    void testProcessRideRequest_successfullyBroadcasts() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(geocodingService.getLatAndLong("Koramangala")).thenReturn(new double[]{12.9352, 77.6146});
        when(broadcastingService.findAndNotifyTopDrivers(anyDouble(), anyDouble(), eq("Sedan"), anyInt()))
                .thenReturn(List.of(DriverDTO.builder().driverId(UUID.randomUUID()).build()));

        rideRequestService.processRideRequest(requestDTO, userId);

        verify(rideRequestRepository, times(1)).save(any(RideRequest.class));
        verify(notificationService, times(1)).notifyRider(eq(userId), contains("Ride request sent"));
    }

    @Test
    void testProcessRideRequest_noDriversNotifiesRider() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(geocodingService.getLatAndLong("Koramangala")).thenReturn(new double[]{12.9352, 77.6146});
        when(broadcastingService.findAndNotifyTopDrivers(anyDouble(), anyDouble(), eq("Sedan"), anyInt()))
                .thenReturn(List.of());

        rideRequestService.processRideRequest(requestDTO, userId);

        verify(notificationService).notifyRider(eq(userId), contains("No available drivers"));
    }

    @Test
    void testCancelRideRequest_notifiesRider() {
        rideRequestService.cancelRideRequest(userId, "Changed mind");
        verify(notificationService).notifyRider(userId, "Ride cancelled: Changed mind");
    }

    @Test
    void testGenerateRideId_returnsFormattedString() {
        String rideId = rideRequestService.generateRideId();
        assert rideId.startsWith("RIDE_") && rideId.length() > 8;
    }
}
