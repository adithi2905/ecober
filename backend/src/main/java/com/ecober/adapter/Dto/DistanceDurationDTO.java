package com.ecober.adapter.Dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceDurationDTO {
    private double distanceKm;
    private long durationInMins;
    private int durationInTrafficSecs;
}
