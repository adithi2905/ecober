package com.ecober.adapter.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideResponseDTO {
    private DriverDTO driver;
    private double carbonEmission;
    private double emissionSaved;
    private String ecoScore;
    private String feedback;
}
