package com.ecober.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Rider {

    private String riderName;

    private double pickupLatitude;
    private double pickupLongitude;

    private double dropoffLatitude;
    private double dropoffLongitude;

    private String riderPickupLocation;
    private String riderDropOffLocation;

    @Id
    private String riderId;
    
    private String preferredVehicleType;
    private boolean willingToPool;
    
    private double co2Saved;
}
