package com.ecober.domain.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecober.adapter.Dto.DistanceDurationDTO;
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

    @Autowired
    RouteOptimizingService routeOptimizingService;

    DistanceDurationDTO distanceDurationDTO;
        public DriverDTO fetchNearestDriver(String riderId,String riderPickupLocation,String riderDropOffLocation,double pickupLatitude, double pickupLongitude,double dropoffLatitude,double dropoffLongitude,String preferredVehicleType, boolean wilingToPool) {
            Logger.getLogger(riderPickupLocation);
            try{
            distanceDurationDTO=routeOptimizingService.getDistanceAndETA(pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude);
            }
            catch(Exception ex)
            {
            distanceDurationDTO=GeoUtils.haversinDistanceandDuration(pickupLatitude, pickupLongitude, dropoffLatitude, dropoffLongitude);
        }
            double carbonEmission=GeoUtils.calculateEmissions(distanceDurationDTO.getDistanceKm(), preferredVehicleType);
            final List<Driver> availableDrivers =driverRepository.findByDriverLocation(riderPickupLocation);     
            Driver best= availableDrivers.stream()
            .findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No suitable driver found"));
            Route route = routeRepository.findBySourceAndDestination(riderPickupLocation, riderDropOffLocation)
                        .orElseGet(() -> {
                        Route newRoute = Route.builder().routeID(UUID.randomUUID().toString())
    .source(riderPickupLocation)
    .destination(riderDropOffLocation)
    .distanceKm(distanceDurationDTO.getDistanceKm())
    .carbonCost(carbonEmission)
    .estimatedTime(distanceDurationDTO.getDurationInMins()) 
    .build();
    return routeRepository.save(newRoute);
});

        Trip trip = new Trip(riderId, best.getDriverId().toString(),best.getDriverName(), route, LocalDateTime.now());
        tripRepository.save(trip);
            
            return driverMapper.toDto(best);
    
        }

}