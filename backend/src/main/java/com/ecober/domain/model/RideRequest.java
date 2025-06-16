package com.ecober.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String pickupLocation;
    private String dropoffLocation;

    private double pickupLat;
    private double pickupLng;
    private double dropoffLat;
    private double dropoffLng;

    private String preferredVehicleType;
    private boolean willingToPool;

    private LocalDateTime requestedTime;

    @Enumerated(EnumType.STRING)
    private RideRequestStatus status;
}
