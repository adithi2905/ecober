package com.ecober.domain.model;
public class Route
{
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