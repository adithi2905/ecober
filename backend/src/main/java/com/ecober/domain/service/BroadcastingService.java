package com.ecober.domain.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.infrastructure.repository.DriverRepository;

@Service
public class BroadcastingService {

    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper;
    
    public BroadcastingService(DriverRepository driverRepository, DriverMapper driverMapper) {
        this.driverRepository = driverRepository;
        this.driverMapper = driverMapper;
    }

    /**
     * Finds and returns top N drivers nearest to the pickup location within the given radius.
     * 
     * @param pickupLat Rider's pickup latitude
     * @param pickupLng Rider's pickup longitude
     * @param vehicleType Preferred vehicle type
     * @param radiusKm Search radius in kilometers
     * @param topN Max number of drivers to return
     * @return List of DriverDTOs sorted by proximity
     */
    @Transactional(readOnly = true)
    public List<DriverDTO> findAndNotifyTopDrivers(
            double pickupLat,
            double pickupLng,
            String vehicleType,
            double radiusKm,
            int topN
    ) {
        var drivers = driverRepository.findNearbyDrivers(
            pickupLat, pickupLng, vehicleType, radiusKm, topN
        );

        return drivers.stream()
                      .map(driverMapper::toDto)
                      .toList();
    }
}
