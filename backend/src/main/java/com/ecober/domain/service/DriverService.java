package com.ecober.domain.service;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Trip;
import com.ecober.domain.model.TripStatus;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.TripRepository;

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
    private TripService tripService;

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

    public double calculateDriverCO2Impact(UUID driverId) {
        return tripRepository.findByDriverId(driverId)
                .stream()
                .mapToDouble(Trip::getCarbonEmissions)
                .sum();
    }

    public boolean startTrip(UUID driverId) {
    return tripService.startTrip(driverId); 
}

    public boolean endTrip(UUID tripId, UUID driverId) {
        Trip trip = tripRepository.findByTripId(tripId);
        if (trip != null && driverId.equals(trip.getDriverId())) {
            trip.setEndTime(LocalDateTime.now());
            trip.setStatus(TripStatus.COMPLETED);
            tripRepository.save(trip);
            return true;
        }
        return false;
    }

    public long getDriverTripCount(UUID driverId) {
        return tripRepository.findByDriverId(driverId).size();
    }

    public double getDriverAverageEmissionPerTrip(UUID driverId) {
        List<Trip> trips = tripRepository.findByDriverId(driverId);
        if (trips.isEmpty()) return 0.0;
        double totalEmissions = trips.stream().mapToDouble(Trip::getCarbonEmissions).sum();
        return totalEmissions / trips.size();
    }
}
