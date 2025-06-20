package com.ecober.adapter.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationDTO {
    private String address;
    private double latitude;
    private double longitude;
    private double elevation;
}
