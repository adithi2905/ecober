package com.ecober.adapter.Dto;
import java.time.LocalDateTime;
import lombok.*;
import java.time.LocalDateTime;

import com.ecober.domain.model.Route;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TripDTO {
    private Long Id;
    private String userId;
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
}
