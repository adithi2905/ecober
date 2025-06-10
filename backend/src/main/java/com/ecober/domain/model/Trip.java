package com.ecober.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ecober.adapter.Dto.DriverDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "driver_id")
    private String driverId;
    private String driverName;
    private double estimatedEmission;
    private String ecoScore;
    private String feedback;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double carbonEmissions;
    private String status;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trip_id", updatable = false, nullable = false)
    private UUID tripId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private Route route;
}
