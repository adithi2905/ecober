package com.ecober.domain.model;

import lombok.Data;

@Data
public class Driver {
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
