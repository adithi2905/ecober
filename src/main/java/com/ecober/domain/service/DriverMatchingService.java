package com.ecober.domain.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.infrastructure.repository.DriverRepository;

@Service
public class DriverMatchingService
{
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DriverMapper driverMapper;
    
        public DriverDTO fetchNearestDriver(String riderPickupLocation, String riderDropOffLocation, String preferredVehicleType, boolean wilingToPool) {
            final List<Driver> availableDrivers =driverRepository.findByVerifiedDriverTrueAndVehicleTypeAndDriverLocation(preferredVehicleType,riderPickupLocation);
            
            
            Driver best= availableDrivers.stream().sorted((d1, d2) -> Double.compare(d2.getTrustScore(), d1.getTrustScore()))
            .findFirst().orElseThrow(() -> new RuntimeException("No suitable driver found"));
            return driverMapper.toDto(best);
    
        }

}