package com.ecober.adapter.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarbonDTO {
    String riderId;
    double totalEmissions;
    int totalTrips;
    double averageEmissionPerTrip;
    String ecoBadge;

}
