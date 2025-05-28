package com.ecober.domain.service;

import com.ecober.adapter.Dto.DistanceDurationDTO;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RiderRequestService {

    @Autowired
    private RouteService routeService;

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private TripService tripService;

    @Autowired
    private DriverMapper driverMapper;

    public DriverDTO handleRideRequest(RiderDTO riderDTO) {

        // Step 1: Compute Route & Emission
        Route route = routeService.getOrCreateRoute(
                riderDTO.getRiderPickupLocation(),
                riderDTO.getRiderDropOffLocation(),
                riderDTO.getPickupLatitude(),
                riderDTO.getPickupLongitude(),
                riderDTO.getDropoffLatitude(),
                riderDTO.getDropoffLongitude(),
                riderDTO.getPreferredVehicleType()
        );

        // Step 2: Find Best Driver
        Driver driver = driverMatchingService.findBestDriver(
                riderDTO.getRiderPickupLocation(),
                riderDTO.getPreferredVehicleType(),
                riderDTO.isWillingToPool()
        );

        // Step 3: Create Trip
        tripService.createTrip(riderDTO.getRiderId(), driver.getDriverId().toString(), route);

        return driverMapper.toDto(driver);
    }
}
