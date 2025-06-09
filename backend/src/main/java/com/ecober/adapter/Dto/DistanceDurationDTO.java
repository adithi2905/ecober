package com.ecober.adapter.Dto;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DistanceDurationDTO {
    private double distanceKm;
    private long durationInMins;
    private int durationInTrafficSecs;
}
