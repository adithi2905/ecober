package com.ecober.domain.model;

import java.time.LocalDateTime;

import com.ecober.adapter.Dto.DriverDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String userId;
    private String driverId;
    private String driverName;
    private double estimatedEmission;
    private String ecoScore;
    private String feedback;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double carbonEmissions;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;    
}
