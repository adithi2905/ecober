package com.ecober.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Route
{
    @Id
    String routeID;
    String source;
    String destination;
    double distanceKm;
    double carbonCOst;
    double estimatedTime;
    boolean isPooledEligible;

    public Route(String source, String destination, double distanceKm) {
        this.source = source;
        this.destination = destination;
        this.distanceKm = distanceKm;
    }

}