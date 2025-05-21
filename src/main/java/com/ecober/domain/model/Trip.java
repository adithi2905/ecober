package com.ecober.domain.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
public class Trip {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String userId;
    private String driverId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double carbonEmissions;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    public Trip(){}

    public Trip(String userId, String driverId, Route route, LocalDateTime startTime) {
        this.userId = userId;
        this.driverId = driverId;
        this.route = route;
        this.startTime = startTime;
        this.carbonEmissions = route.getDistanceKm() * 0.21; 
    }

    
}
