package com.ecober.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DriverDTO {
    private String driverName;
    private String vehicleNo;
    private String driverId;
    private boolean verifiedDriver;
    private String driverLocation;

    private String vehicleType;
    private double fuelEfficiency;
    private double trustScore;
    private double totalCO2Saved;

}
