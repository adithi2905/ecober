package com.ecober.domain.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ecober.adapter.Dto.DriverDTO;

@Service
public class FetchNearestRiderService
{

    private final List<DriverDTO>availableDrivers=List.of(new DriverDTO("Alice", "KA01X123", "D001", true, "Indiranagar", "EV", 18.5, 4.5, 12.0),
        new DriverDTO("Bob", "KA02Y456", "D002", true, "MG Road", "SUV", 10.0, 4.2, 20.0),
        new DriverDTO("Charlie", "KA03Z789", "D003", false, "Indiranagar", "Sedan", 15.0, 3.9, 5.0));

        public DriverDTO fetchNearestRider(String riderPickupLocation, String riderDropOffLocation, String preferredVehicleType, boolean wilingToPool) {
            for(DriverDTO driver:availableDrivers)
            {
                if(driver.getDriverLocation().equalsIgnoreCase(riderDropOffLocation) &&
                (driver.getVehicleType().equalsIgnoreCase(preferredVehicleType)) &&
                (driver.isVerifiedDriver()))
                return driver;
                
            }
            throw new RuntimeException("No suitable driver found");
    }

}