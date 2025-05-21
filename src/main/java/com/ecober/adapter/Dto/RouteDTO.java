package com.ecober.adapter.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RouteDTO {

    private String source;
    private String destination;
    private double distanceKm;
    private double carbonCOst;
    private double estimatedTime;
    private boolean isPooledEligible;

}
