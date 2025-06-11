package com.ecober.adapter.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DriverRegistrationRequestDTO {
    private String name;
    private String email;
    private String password;
}
