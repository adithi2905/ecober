package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.adapter.Dto.TripDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.adapter.mapper.RideRequestMapper;
import com.ecober.adapter.mapper.TripperMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.RideRequestStatus;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.domain.model.TripStatus;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.RideRequestRepository;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.util.GeoUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RouteOptimizingService routeOptimizingService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private TripperMapper tripMapper;

    @Autowired
    private TripService tripService;

    @Autowired
    private RideRequestMapper rideRequestMapper;

    @Autowired
    RideRequestRepository rideRequestRepository;
        

    // Registration
    public void register(DriverRegistrationRequestDTO request) {
        Driver driver = new Driver();
        driver.setDriverName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
        driver.setDriverLocation(request.getLocation());
        driver.setVerifiedDriver(request.isVerified());
        driver.setVehicleNo(request.getVehicleNo());
        driver.setVehicleType(request.getVehicleType());
        driverRepository.save(driver);
    }

    // Authentication
    public Driver authenticate(DriverAuthenticationRequest request) {
        Driver driver = driverRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return driver;
    }

    // CRUD & Profile Operations
    public List<DriverDTO> getAllDrivers() {
        return driverMapper.toDtoList(driverRepository.findAll());
    }

    public Optional<DriverDTO> getDriverById(UUID driverId) {
        return driverRepository.findByDriverId(driverId)
                .map(driverMapper::toDto);
    }

    public List<DriverDTO> getDriversByLocation(String location) {
        return driverMapper.toDtoList(driverRepository.findByDriverLocation(location));
    }

    public DriverDTO createDriver(DriverDTO driverDTO) {
        Driver driver = driverMapper.toEntity(driverDTO);
        Driver savedDriver = driverRepository.save(driver);
        return driverMapper.toDto(savedDriver);
    }

    public Optional<DriverDTO> updateDriver(UUID driverId, DriverDTO driverDTO) {
        return driverRepository.findById(driverId)
                .map(existingDriver -> {
                    existingDriver.setDriverName(driverDTO.getDriverName());
                    existingDriver.setVehicleNo(driverDTO.getVehicleNo());
                    existingDriver.setVerifiedDriver(driverDTO.isVerifiedDriver());
                    existingDriver.setDriverLocation(driverDTO.getDriverLocation());
                    existingDriver.setVehicleType(driverDTO.getVehicleType());
                    existingDriver.setFuelEfficiency(driverDTO.getFuelEfficiency());
                    existingDriver.setTrustScore(driverDTO.getTrustScore());
                    existingDriver.setTotalCO2Saved(driverDTO.getTotalCO2Saved());
                    return driverMapper.toDto(driverRepository.save(existingDriver));
                });
    }

    public boolean deleteDriver(UUID driverId) {
        if (driverRepository.existsById(driverId)) {
            driverRepository.deleteById(driverId);
            return true;
        }
        return false;
    }

    public boolean startTrip(UUID driverId) {
        return tripService.startTrip(driverId);
    }

    public boolean endTrip(UUID tripId, UUID driverId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip != null && trip.getDriver() != null &&
            driverId.equals(trip.getDriver().getDriverId())) {
            trip.setEndTime(LocalDateTime.now());
            trip.setStatus(TripStatus.COMPLETED);
            tripRepository.save(trip);
            return true;
        }
        return false;
    }

    public List<RideRequestDTO> getNearbyAvailableTrips(UUID driverId) {
        Driver driver = driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        String location = driver.getDriverLocation();
        String vehicleType = driver.getVehicleType();
        List<RideRequest> availableTrips = rideRequestRepository.findNearbyRideRequests(location, vehicleType,RideRequestStatus.REQUESTED);
        return rideRequestMapper.toDtoList(availableTrips);
    }

    public long getDriverTripCount(UUID driverId) {
        return tripRepository.findByDriver_DriverId(driverId).size();
    }

        public TripDTO acceptRide(UUID rideRequestId, UUID driverId) {
    RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
            .orElseThrow(() -> new IllegalArgumentException("Ride request not found."));

    if (rideRequest.getStatus() != RideRequestStatus.REQUESTED) {
        throw new IllegalStateException("Ride has already been accepted or is no longer available.");
    }

    Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new IllegalArgumentException("Driver not found."));

    double[] pickupLatLong = geocodingService.getLatAndLong(rideRequest.getPickupLocation());
    double[] dropoffLatLong = geocodingService.getLatAndLong(rideRequest.getDropoffLocation());

    DistanceDurationDTO distanceDTO;
    try {
        distanceDTO = routeOptimizingService.getDistanceAndETA(
                new Location(pickupLatLong[0], pickupLatLong[1], rideRequest.getPickupLocation(), 0),
                new Location(dropoffLatLong[0], dropoffLatLong[1], rideRequest.getDropoffLocation(), 0)
        );
    } catch (Exception ex) {
        distanceDTO = GeoUtils.haversinDistanceandDuration(
                pickupLatLong[0], pickupLatLong[1], dropoffLatLong[0], dropoffLatLong[1]
        );
    }

    double carbonEmission = GeoUtils.calculateEmissions(distanceDTO.getDistanceKm(), rideRequest.getPreferredVehicleType());

    Route route = Route.builder()
            .source(new Location(pickupLatLong[0], pickupLatLong[1], rideRequest.getPickupLocation(), 0))
            .destination(new Location(dropoffLatLong[0], dropoffLatLong[1], rideRequest.getDropoffLocation(), 0))
            .distanceKm(distanceDTO.getDistanceKm())
            .estimatedTime(distanceDTO.getDurationInMins())
            .carbonEmission(carbonEmission)
            .isPooledEligible(rideRequest.isWillingToPool())
            .build();

    Trip trip = Trip.builder()
            .user(rideRequest.getUser())
            .driver(driver)
            .route(route)
            .status(TripStatus.IN_PROGRESS)
            .startTime(LocalDateTime.now())
            .estimatedEmission(carbonEmission)
            .build();

    tripRepository.save(trip);
    rideRequestRepository.delete(rideRequest);

    return tripMapper.toDto(trip);
}

    public double getDriverAverageEmissionPerTrip(UUID driverId) {
        List<Trip> trips = tripRepository.findByDriver_DriverId(driverId);
        if (trips.isEmpty()) return 0.0;
        double totalEmissions = trips.stream()
        .mapToDouble(Trip::getEstimatedEmission)
        .sum();
        return totalEmissions / trips.size();
    }

    public double calculateDriverCO2Impact(UUID driverId) {
        return tripRepository.findByDriver_DriverId(driverId)
                .stream()
                .mapToDouble(Trip::getEstimatedEmission)
                .sum();
    }
}
