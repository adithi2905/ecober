package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Trip;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, DriverMapper.class, RouteMapper.class})
public interface TripperMapper {

    TripDTO toDto(Trip trip);

    Trip toEntity(TripDTO tripDTO);

    List<TripDTO> toDtoList(List<Trip> trips);

    List<Trip> toEntityList(List<TripDTO> tripDTOs);
}
