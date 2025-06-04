package com.ecober.adapter.Dto;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DistanceDurationDTO {
    private double distanceKm;
    private long durationInMins;
    private int durationInTrafficSecs;
}
