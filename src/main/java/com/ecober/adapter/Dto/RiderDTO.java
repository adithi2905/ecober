package com.ecober.adapter.Dto;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderDTO {

    private String riderName;
    private String riderPickupLocation;
    private String riderDropOffLocation;
    private double pickupLatitude;
    private double pickupLongitude;
    private double dropoffLatitude;
    private double dropoffLongitude;
    private String riderId;
    private String preferredVehicleType;
    private boolean wilingToPool;
    private double co2Saved;

}
