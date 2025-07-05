package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.RouteDTO;
import com.ecober.domain.model.Route;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = LocationMapper.class)
public interface RouteMapper {
    RouteDTO toDto(Route route);
    Route toEntity(RouteDTO dto);
}
