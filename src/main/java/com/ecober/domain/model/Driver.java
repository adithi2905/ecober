package com.ecober.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="drivers")
public class Driver {
    @Id
    private String driverId;
    private String driverName;
    private String vehicleNo;
    private boolean verifiedDriver;
    private String driverLocation;

    private String vehicleType;
    private double fuelEfficiency;
    private double trustScore;
    private double totalCO2Saved;

}
