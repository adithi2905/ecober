package com.ecober.adapter.mapper;

import org.mapstruct.Mapper;
import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.domain.model.Driver;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    DriverDTO toDto(Driver driver);
    Driver toEntity(DriverDTO driverDTO);

    List<DriverDTO> toDtoList(List<Driver> drivers);
    List<Driver> toEntityList(List<DriverDTO> driverDTOs);
}
