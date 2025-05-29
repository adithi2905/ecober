package com.ecober.domain.service;

import com.ecober.domain.model.Driver;
import com.ecober.infrastructure.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class DriverMatchingService {

    @Autowired
    private DriverRepository driverRepository;

    public Driver findBestDriver(String location, String vehicleType, boolean willingToPool) {
        List<Driver> drivers = driverRepository.findByDriverLocation(location);
        return drivers.stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable driver found"));
    }
}
