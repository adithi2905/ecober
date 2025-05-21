package com.ecober.adapter.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Trip;


@Mapper(componentModel = "spring")
public interface TripMapper {

    TripDTO toDto(Trip results);
    Trip toEntity(TripDTO tripDTO);

    List<TripDTO> toDtoList(List<Trip> trips);
    List<Trip> toEntityList(List<TripDTO> tripsDTO);
}
    

