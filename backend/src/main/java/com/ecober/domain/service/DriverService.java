package com.ecober.domain.service;

import com.ecober.adapter.Dto.*;
import com.ecober.adapter.mapper.*;
import com.ecober.domain.model.*;
import com.ecober.infrastructure.repository.*;
import com.ecober.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired private DriverRepository driverRepository;
    @Autowired private TripRepository tripRepository;
    @Autowired private DriverMapper driverMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RouteOptimizingService routeOptimizingService;
    @Autowired private GeocodingService geocodingService;
    @Autowired private TripperMapper tripMapper;
    @Autowired private RideRequestMapper rideRequestMapper;
    @Autowired private RideRequestRepository rideRequestRepository;
    @Autowired
    private RedisTemplate<String, UUID> redisTemplate;


    public void register(DriverRegistrationRequestDTO request) {
        Driver driver = Driver.builder()
                .driverName(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .driverLocation(request.getLocation())
                .verifiedDriver(request.isVerified())
                .vehicleNo(request.getVehicleNo())
                .vehicleType(request.getVehicleType())
                .build();
        driverRepository.save(driver);
    }

    public Driver authenticate(DriverAuthenticationRequest request) {
        Driver driver = driverRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Driver not found"));
        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return driver;
    }

    public List<DriverDTO> getAllDrivers() {
        return driverMapper.toDtoList(driverRepository.findAll());
    }

    public Optional<DriverDTO> getDriverById(UUID driverId) {
        return driverRepository.findByDriverId(driverId).map(driverMapper::toDto);
    }

    public List<DriverDTO> getDriversByLocation(String location) {
        return driverMapper.toDtoList(driverRepository.findByDriverLocation(location));
    }

    public DriverDTO createDriver(DriverDTO driverDTO) {
        Driver driver=driverMapper.toEntity(driverDTO);
        driver.setRole("DRIVER");
        Driver savedDriver = driverRepository.save(driver);
        return driverMapper.toDto(savedDriver);
    }

    public Optional<DriverDTO> updateDriver(UUID driverId, DriverDTO driverDTO) {
        return driverRepository.findById(driverId).map(driver -> {
            driver.setDriverName(driverDTO.getDriverName());
            driver.setVehicleNo(driverDTO.getVehicleNo());
            driver.setVerifiedDriver(driverDTO.isVerifiedDriver());
            driver.setDriverLocation(driverDTO.getDriverLocation());
            driver.setVehicleType(driverDTO.getVehicleType());
            driver.setFuelEfficiency(driverDTO.getFuelEfficiency());
            driver.setTrustScore(driverDTO.getTrustScore());
            driver.setTotalCO2Saved(driverDTO.getTotalCO2Saved());
            return driverMapper.toDto(driverRepository.save(driver));
        });
    }

    public boolean deleteDriver(UUID driverId) {
        if (driverRepository.existsById(driverId)) {
            driverRepository.deleteById(driverId);
            return true;
        }
        return false;
    }

    public Optional<TripDTO> getCurrentTripForDriver(UUID driverId) {
        return Optional.ofNullable(tripMapper.toDto(tripRepository.findAcceptedRide(driverId)));
    }

    public TripDTO startTrip(UUID tripId, UUID driverId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip != null &&
            trip.getDriver() != null &&
            driverId.equals(trip.getDriver().getDriverId()) &&
            trip.getStatus() == TripStatus.ACCEPTED) {

            trip.setStartTime(LocalDateTime.now());
            trip.setStatus(TripStatus.IN_PROGRESS);
            return tripMapper.toDto(tripRepository.save(trip));
        }
        throw new IllegalStateException("Trip cannot be started");
    }

    public boolean endTrip(UUID tripId, UUID driverId) {
    
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip != null &&
            trip.getDriver() != null &&
            driverId.equals(trip.getDriver().getDriverId())) {
            trip.setEndTime(LocalDateTime.now());
            trip.setStatus(TripStatus.COMPLETED);
            tripRepository.save(trip);
            redisTemplate.delete("active_trip:" + driverId);
            redisTemplate.delete("active_ride:" + trip.getUser().getUserId());
            return true;
        }
        return false;
    }

    public List<RideRequestDTO> getNearbyAvailableTrips(UUID driverId) {
        Driver driver = driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        List<RideRequest> nearby = rideRequestRepository.findNearbyRideRequests(
                driver.getDriverLocation(),
                driver.getVehicleType(),
                RideRequestStatus.REQUESTED
        );
        return rideRequestMapper.toDtoList(nearby);
    }

    public long getDriverTripCount(UUID driverId) {
        return tripRepository.findByDriver_DriverId(driverId).size();
    }

    @Transactional
    public TripDTO acceptRide(UUID rideRequestId, UUID driverId) {

        String redisKey = "active_trip:" + driverId;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            throw new IllegalStateException("Driver already has an active trip.");
        }
            
        RideRequest request = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Ride request not found."));
        if (request.getStatus() != RideRequestStatus.REQUESTED)
            throw new IllegalStateException("Ride already accepted or unavailable.");

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found."));

        double[] pickup = geocodingService.getLatAndLong(request.getPickupLocation());
        double[] dropoff = geocodingService.getLatAndLong(request.getDropoffLocation());

        DistanceDurationDTO distance;
        try {
            distance = routeOptimizingService.getDistanceAndETA(
                    new Location(pickup[0], pickup[1], request.getPickupLocation(), 0),
                    new Location(dropoff[0], dropoff[1], request.getDropoffLocation(), 0)
            ); 
        } catch (Exception e) {
            distance = GeoUtils.calculateDistanceAndDuration(pickup[0], pickup[1], dropoff[0], dropoff[1]);
        }

        double emission = GeoUtils.calculateEmissions(distance.getDistanceKm(), request.getPreferredVehicleType());

        Trip trip = Trip.builder()
                .user(request.getUser())
                .driver(driver)
                .route(Route.builder()
                        .source(new Location(pickup[0], pickup[1], request.getPickupLocation(), 0))
                        .destination(new Location(dropoff[0], dropoff[1], request.getDropoffLocation(), 0))
                        .distanceKm(distance.getDistanceKm())
                        .estimatedTime(distance.getDurationInMins())
                        .carbonEmission(emission)
                        .isPooledEligible(request.isWillingToPool())
                        .build())
                .status(TripStatus.ACCEPTED)
                .estimatedEmission(emission).vehicleType(request.getPreferredVehicleType())
                .build();

        Trip savedTrip=tripRepository.save(trip);
        tripRepository.save(savedTrip);
        rideRequestRepository.deleteById(request.getId());
        redisTemplate.opsForValue().set(redisKey, trip.getTripId(),Duration.ofMinutes(30));

        return tripMapper.toDto(trip);
    }

    public double getDriverAverageEmissionPerTrip(UUID driverId) {
        List<Trip> trips = tripRepository.findByDriver_DriverId(driverId);
        if (trips.isEmpty()) return 0.0;
        return trips.stream().mapToDouble(Trip::getEstimatedEmission).average().orElse(0.0);
    }

    public double calculateDriverCO2Impact(UUID driverId) {
        return tripRepository.findByDriver_DriverId(driverId)
                .stream()
                .mapToDouble(Trip::getEstimatedEmission)
                .sum();
    }

    public double getCurrentMonthCO2Savings(UUID driverId) {
    LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
    return tripRepository.findByDriver_DriverId(driverId).stream()
        .filter(trip -> trip.getEndTime() != null && trip.getEndTime().isAfter(startOfMonth))
        .mapToDouble(Trip::getEstimatedEmission)
        .sum();
}


    public List<Map<String, Object>> getRideTypeDistribution(UUID driverId) {
    return tripRepository.findByDriver_DriverId(driverId).stream()
        .map(Trip::getVehicleType)
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(String::toUpperCase, Collectors.counting()))
        .entrySet().stream()
        .map(entry -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", entry.getKey());
            map.put("value", entry.getValue());
            return map;
        })
        .collect(Collectors.toList());
}

}

