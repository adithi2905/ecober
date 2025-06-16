package com.ecober.adapter.Dto;

import lombok.*;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequestDTO {

    private UUID rideRequestId;

    @NotBlank(message = "Pickup location is required")
    private String pickupLocation;

    @NotBlank(message = "Dropoff location is required")
    private String dropoffLocation;

    @NotBlank(message = "Preferred vehicle type is required")
    private String preferredVehicleType;

    private boolean willingToPool;
}
