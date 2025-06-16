package com.ecober.adapter.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripDTO {

    private UUID tripId;
    private UUID riderId;
    private UUID driverId;

    private String driverName;
    private DriverDTO driver;
    private UserDTO user;

    private RouteDTO route;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double estimatedEmission;
    private double actualEmission;
    private String ecoScore;

    private String feedback;
    private String status; 
}
