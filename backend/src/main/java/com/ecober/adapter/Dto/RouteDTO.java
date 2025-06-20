package com.ecober.adapter.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteDTO {
    private LocationDTO source;
    private LocationDTO destination;
    private double distanceKm;
    private double carbonCost;
    private double estimatedTime;
    private boolean isPooledEligible;
    private double carbonEmission;
}
