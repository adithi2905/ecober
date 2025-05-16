package com.ecober.domain.model;

import lombok.Data;

@Data
public class Rider {

    private String riderName;
    private String riderPickupLocation;
    private String riderDropOffLocation;
    private String riderId;
    private String preferredVehicleType;
    private boolean wilingToPool;
    private double co2Saved;

}
