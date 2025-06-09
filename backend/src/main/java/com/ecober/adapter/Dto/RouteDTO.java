package com.ecober.adapter.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RouteDTO {

    private String source;
    private String destination;
    private double distanceKm;
    private double carbonCost;
    private double carbonEmission;
    private double estimatedTime;
    private boolean isPooledEligible;

}
