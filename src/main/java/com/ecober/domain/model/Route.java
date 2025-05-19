package com.ecober.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route
{
    @Id
    String routeID;
    String source;
    String destination;
    @Column(name = "distance_km", nullable = false)
    double distanceKm;
    @Column(name = "carbon_cost", nullable = false)
    double carbonCost;
    @Column(name = "estimated_time", nullable = false)
    double estimatedTime;
    @Column(name = "is_pooled_eligible", nullable = false)
    boolean isPooledEligible;


}