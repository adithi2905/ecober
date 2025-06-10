package com.ecober.domain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverAuthenticationRequest;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.DriverRegistrationRequestDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.TripRepository;

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

     public void register(DriverRegistrationRequestDTO request) {
        Driver driver = new Driver();
        driver.setDriverName(request.getName());
        driver.setEmail(request.getEmail());
        driver.setPassword(passwordEncoder.encode(request.getPassword()));
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
        List<Driver> drivers = driverRepository.findAll();
        return driverMapper.toDtoList(drivers);
    }
    
    public Optional<DriverDTO> getDriverById(String driverId) {
        return driverRepository.findById(driverId)
                .map(driverMapper::toDto);
    }
    
    public List<DriverDTO> getDriversByLocation(String location) {
        List<Driver> drivers = driverRepository.findByDriverLocation(location);
        return driverMapper.toDtoList(drivers);
    }
    
    public DriverDTO createDriver(DriverDTO driverDTO) {
        Driver driver = driverMapper.toEntity(driverDTO);
        Driver savedDriver = driverRepository.save(driver);
        return driverMapper.toDto(savedDriver);
    }
    
    public Optional<DriverDTO> updateDriver(String driverId, DriverDTO driverDTO) {
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
                    
                    Driver updatedDriver = driverRepository.save(existingDriver);
                    return driverMapper.toDto(updatedDriver);
                });
    }
    
    public boolean deleteDriver(String driverId) {
        if (driverRepository.existsById(driverId)) {
            driverRepository.deleteById(driverId);
            return true;
        }
        return false;
    }
    
    public double calculateDriverCO2Impact(String driverId) {
        List<Trip> driverTrips = tripRepository.findByDriverId(driverId);
        return driverTrips.stream()
                .mapToDouble(Trip::getCarbonEmissions)
                .sum();
    }
    
    public long getDriverTripCount(String driverId) {
        return tripRepository.findByDriverId(driverId).size();
    }
    
    public double getDriverAverageEmissionPerTrip(String driverId) {
        List<Trip> trips = tripRepository.findByDriverId(driverId);
        if (trips.isEmpty()) return 0.0;
        
        double totalEmissions = trips.stream()
                .mapToDouble(Trip::getCarbonEmissions)
                .sum();
        return totalEmissions / trips.size();
    }
}
