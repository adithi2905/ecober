package com.ecober.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID tripId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @Embedded
    private Route route;
    private String ecoScore;

    @Column(name = "actual_emission")
    private double carbonEmission;

    private String vehicleType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private User user;

    @Enumerated(EnumType.STRING)
    private TripStatus status; // ACCEPTED, IN_PROGRESS, COMPLETED
}
