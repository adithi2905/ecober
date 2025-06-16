package com.ecober.adapter.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Location;
import com.ecober.domain.model.Trip;

@Mapper(componentModel = "spring")
public interface TripperMapper {

    @Mappings({
        @Mapping(source = "route.source", target = "route.source", qualifiedByName = "locationToString"),
        @Mapping(source = "route.destination", target = "route.destination", qualifiedByName = "locationToString")
    })
    TripDTO toDto(Trip trip);

    @Mappings({
        @Mapping(source = "route.source", target = "route.source", qualifiedByName = "stringToLocation"),
        @Mapping(source = "route.destination", target = "route.destination", qualifiedByName = "stringToLocation")
    })
    Trip toEntity(TripDTO tripDTO);

    List<TripDTO> toDtoList(List<Trip> trips);

    List<Trip> toEntityList(List<TripDTO> tripDTOs);

    @Named("locationToString")
    static String locationToString(Location location) {
        return location == null ? null : location.getAddress();
    }

    @Named("stringToLocation")
    static Location stringToLocation(String address) {
        if (address == null) return null;
        Location location = new Location();
        location.setAddress(address); 
        return location;
    }
}
