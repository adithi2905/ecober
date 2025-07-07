package com.ecober.domain.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="driver")
public class Driver {

     @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID driverId;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private boolean enabled = true;

    private String driverName;
    private String vehicleNo;
    private boolean verifiedDriver;
    private String driverLocation;

    private String vehicleType;
    private double fuelEfficiency;
    private double trustScore;
    private double totalCO2Saved;
    private String role="DRIVER";
    private String vin;

}
