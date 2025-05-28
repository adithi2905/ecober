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

        Route route = routeService.getOrCreateRoute(
                riderDTO.getRiderPickupLocation(),
                riderDTO.getRiderDropOffLocation(),
                riderDTO.getPickupLatitude(),
                riderDTO.getPickupLongitude(),
                riderDTO.getDropoffLatitude(),
                riderDTO.getDropoffLongitude(),
                riderDTO.getPreferredVehicleType()
        );

        Driver driver = driverMatchingService.findBestDriver(
                riderDTO.getRiderPickupLocation(),
                riderDTO.getPreferredVehicleType(),
                riderDTO.isWillingToPool()
        );

        tripService.createTrip(riderDTO.getRiderId(), driver.getDriverId().toString(), route);

        return driverMapper.toDto(driver);
    }
}
