package com.ecober.domain.service;

import com.ecober.adapter.Dto.*;
import com.ecober.adapter.mapper.*;
import com.ecober.domain.model.*;
import com.ecober.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock private DriverRepository driverRepository;
    @Mock private TripRepository tripRepository;
    @Mock private RideRequestRepository rideRequestRepository;
    @Mock private RouteOptimizingService routeOptimizingService;
    @Mock private GeocodingService geocodingService;
    @Mock private DriverMapper driverMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TripperMapper tripperMapper;
    @Mock private RideRequestMapper rideRequestMapper;
    @Mock private TripService tripService;

    @InjectMocks
    private DriverService driverService;

    private UUID driverId;
    private Driver driver;
    private Trip trip;
    private RideRequest rideRequest;
    private User user;

    @BeforeEach
    void setUp() {
        driverId = UUID.randomUUID();
        driver = Driver.builder().driverId(driverId).driverLocation("Koramangala").vehicleType("Sedan").build();
        user = User.builder().userId(UUID.randomUUID()).username("Adithi").build();
        rideRequest = RideRequest.builder()
                .pickupLocation("Koramangala")
                .dropoffLocation("MG Road")
                .preferredVehicleType("Sedan")
                .willingToPool(false)
                .status(RideRequestStatus.REQUESTED)
                .user(user)
                .build();
        trip = Trip.builder()
                .tripId(UUID.randomUUID())
                .driver(driver)
                .user(user)
                .status(TripStatus.ACCEPTED)
                .estimatedEmission(1.0)
                .build();
    }

    @Test
    void testRegister() {
        DriverRegistrationRequestDTO request = new DriverRegistrationRequestDTO(
            "John",
            "john@mail.com",
            "pass",
            "Bangalore",
            "SUV",
            true,
            "KA-01-AA-1234"
        );

        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        driverService.register(request);

        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void testAuthenticate_success() {
        Driver driver = Driver.builder().email("john@example.com").password("encoded").build();
        when(driverRepository.findByEmail("john@example.com")).thenReturn(Optional.of(driver));
        when(passwordEncoder.matches("1234", "encoded")).thenReturn(true);

        Driver result = driverService.authenticate(new DriverAuthenticationRequest("john@example.com", "1234"));

        assertThat(result).isEqualTo(driver);
    }

    @Test
    void testAuthenticate_fail_invalidPassword() {
        Driver driver = Driver.builder().email("john@example.com").password("encoded").build();
        when(driverRepository.findByEmail("john@example.com")).thenReturn(Optional.of(driver));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(Exception.class, () -> driverService.authenticate(new DriverAuthenticationRequest("john@example.com", "wrong")));
    }

    @Test
    void testGetDriverTripCount() {
        when(tripRepository.findByDriver_DriverId(driverId)).thenReturn(List.of(trip, trip));

        long count = driverService.getDriverTripCount(driverId);

        assertThat(count).isEqualTo(2);
    }

    @Test
    void testCalculateDriverCO2Impact() {
        when(tripRepository.findByDriver_DriverId(driverId)).thenReturn(List.of(trip));

        double impact = driverService.calculateDriverCO2Impact(driverId);

        assertThat(impact).isEqualTo(1.0);
    }

    @Test
    void testGetDriverAverageEmissionPerTrip() {
        Trip trip2 = Trip.builder().estimatedEmission(3.0).build();
        when(tripRepository.findByDriver_DriverId(driverId)).thenReturn(List.of(trip, trip2));

        double avg = driverService.getDriverAverageEmissionPerTrip(driverId);

        assertThat(avg).isEqualTo(2.0);
    }
}
