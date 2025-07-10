package com.ecober.adapter.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private UUID userId;
    private String username;
    private double totalCO2Saved;    
    private double averageCO2Saved;  
    private int tripCount;           
    private String ecoBadge;         
}
