package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.LocationDTO;
import com.ecober.domain.model.Location;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    LocationDTO toDto(Location location);
    Location toEntity(LocationDTO dto);
}
