package com.ecober.adapter.Dto;
import java.time.LocalDateTime;
import lombok.*;

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
    private String driverName; // <== Added this
    private double estimatedEmission;
    private String ecoScore;
    private String feedback;
}
