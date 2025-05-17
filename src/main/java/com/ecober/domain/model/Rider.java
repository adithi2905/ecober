package com.ecober.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Rider {

    private String riderName;
    private String riderPickupLocation;
    private String riderDropOffLocation;
    @Id
    private String riderId;
    private String preferredVehicleType;
    private boolean wilingToPool;
    private double co2Saved;

}
