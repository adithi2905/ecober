package com.ecober.adapter.Dto;

import lombok.NoArgsConstructor;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderDTO {
    private String riderName;

    @NotNull
    private String riderPickupLocation;
    @NotNull
    private String riderDropOffLocation;
    private double pickupLatitude;
    private double pickupLongitude;
    private double dropoffLatitude;
    private double dropoffLongitude;
    private UUID riderId;
    @Nullable
    private String preferredVehicleType;
    @Nullable
    private boolean willingToPool;
    @Nullable
    private double co2Saved;
}
