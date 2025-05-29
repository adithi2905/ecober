package com.ecober.domain.service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.Dto.RiderDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private GeocodingService geocodingService;

    public DriverDTO handleRideRequest(RiderDTO riderDTO) {

        double[] pickupLatLong = geocodingService.getLatAndLong(riderDTO.getRiderPickupLocation());
        double[] dropoffLatLong = geocodingService.getLatAndLong(riderDTO.getRiderDropOffLocation());

        RiderDTO updatedRiderDTO = RiderDTO.builder()
                .riderId(riderDTO.getRiderId())
                .riderName(riderDTO.getRiderName())
                .riderPickupLocation(riderDTO.getRiderPickupLocation())
                .riderDropOffLocation(riderDTO.getRiderDropOffLocation())
                .pickupLatitude(pickupLatLong[0])
                .pickupLongitude(pickupLatLong[1])
                .dropoffLatitude(dropoffLatLong[0])
                .dropoffLongitude(dropoffLatLong[1])
                .preferredVehicleType(riderDTO.getPreferredVehicleType())
                .willingToPool(riderDTO.isWillingToPool())
                .build();

        Route route = routeService.getOrCreateRoute(
                updatedRiderDTO.getRiderPickupLocation(),
                updatedRiderDTO.getRiderDropOffLocation(),
                updatedRiderDTO.getPickupLatitude(),
                updatedRiderDTO.getPickupLongitude(),
                updatedRiderDTO.getDropoffLatitude(),
                updatedRiderDTO.getDropoffLongitude(),
                updatedRiderDTO.getPreferredVehicleType()
        );

        Driver driver = driverMatchingService.findBestDriver(
                updatedRiderDTO.getRiderPickupLocation(),
                updatedRiderDTO.getPreferredVehicleType(),
                updatedRiderDTO.isWillingToPool()
        );

        tripService.createTrip(updatedRiderDTO.getRiderId(), driver.getDriverId().toString(), route);

        return driverMapper.toDto(driver);
    }
}
