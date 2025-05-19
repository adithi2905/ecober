package com.ecober.domain.service;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.domain.model.Route;
import com.ecober.domain.model.Trip;
import com.ecober.infrastructure.repository.DriverRepository;
import com.ecober.infrastructure.repository.RouteRepository;
import com.ecober.infrastructure.repository.TripRepository;
import com.ecober.util.GeoUtils;

@Service
public class DriverMatchingService
{
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    DriverMapper driverMapper;

        public DriverDTO fetchNearestDriver(String riderId,String riderPickupLocation,String riderDropOffLocation,double pickupLatitude, double pickupLongitude,double dropoffLatitude,double dropoffLongitude,String preferredVehicleType, boolean wilingToPool) {
            final List<Driver> availableDrivers =driverRepository.findByDriverLocation(riderPickupLocation);     
            Driver best= availableDrivers.stream()
            .findFirst().orElseThrow(() -> new RuntimeException("No suitable driver found"));
            
            Route route = routeRepository.findBySourceAndDestination(riderPickupLocation, riderDropOffLocation)
                        .orElseGet(() -> {
                        Route newRoute = Route.builder()
    .source(riderPickupLocation)
    .destination(riderDropOffLocation)
    .distanceKm(GeoUtils.haversinDistance(pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude))
    .carbonCost(0.0)
    .estimatedTime(0.0) 
    .build();
    return routeRepository.save(newRoute);
});

        Trip trip = new Trip(riderId, best.getDriverId().toString(), route, LocalDateTime.now());
        tripRepository.save(trip);
            
            return driverMapper.toDto(best);
    
        }

}