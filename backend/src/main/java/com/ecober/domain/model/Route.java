package com.ecober.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    private String routeID;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "source_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "source_longitude")),
        @AttributeOverride(name = "address", column = @Column(name = "source_address")),
        @AttributeOverride(name = "elevation", column = @Column(name = "source_elevation"))
    })
    private Location source;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "destination_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "destination_longitude")),
        @AttributeOverride(name = "address", column = @Column(name = "destination_address")),
        @AttributeOverride(name = "elevation", column = @Column(name = "destination_elevation"))
    })
    private Location destination;

    @Column(name = "distance_km", nullable = false)
    private double distanceKm;

    @Column(name = "carbon_cost", nullable = false)
    private double carbonCost;

    @Column(name = "estimated_time", nullable = false)
    private double estimatedTime;

    @Column(name = "is_pooled_eligible", nullable = false)
    private boolean isPooledEligible;

    private double carbonEmission;
}
