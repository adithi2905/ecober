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
    private UUID riderId;
    private String driverId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double carbonEmissions;

    private RouteDTO routedto;
    private DriverDTO driver;
    private String driverName;
    private double estimatedEmission;
    private String ecoScore;
    private String feedback;
    private String status; 
}
