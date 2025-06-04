package com.ecober.adapter.Dto;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
public class DistanceDurationDTO {
    private double distanceKm;
    private long durationInMins;
    private int durationInTrafficSecs;
}
