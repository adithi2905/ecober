
package com.ecober.domain.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.util.GeoUtils;

@Service
public class BroadcastingService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private DriverMapper driverMapper;

    public List<DriverDTO> findAndNotifyTopDrivers(
            double pickupLat,
            double pickupLng,
            String vehicleType,
            int topN
    ) {
        List<Driver> topDrivers = driverRepository.findAll().stream()
                .filter(d -> vehicleType.equalsIgnoreCase(d.getVehicleType()))
                .sorted(Comparator.comparingDouble(driver -> {
                    double[] coords = geocodingService.getLatAndLong(driver.getDriverLocation());
                    return GeoUtils.haversinDistance(pickupLat, pickupLng, coords[0], coords[1]);
                }))
                .limit(topN)
                .collect(Collectors.toList());

        return topDrivers.stream().map(driverMapper::toDto).collect(Collectors.toList());
    }
}
