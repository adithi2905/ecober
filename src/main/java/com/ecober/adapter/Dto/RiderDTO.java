package com.ecober.adapter.Dto;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RiderDTO {

    private String riderName;
    private String riderPickupLocation;
    private String riderDropOffLocation;
    private String riderId;
    private String preferredVehicleType;
    private boolean wilingToPool;
    private double co2Saved;

}
