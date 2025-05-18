package com.ecober.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
@Builder
public class Route
{
    @Id
    String routeID;
    String source;
    String destination;
    double distanceKm;
    double carbonCost;
    double estimatedTime;
    boolean isPooledEligible;


}