package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.RideRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RideRequestMapper {

    @Mapping(source = "id", target = "rideRequestId")
    RideRequestDTO toDto(RideRequest rideRequest);

    @Mapping(source = "rideRequestId", target = "id")
    @Mapping(target = "pickupLat", ignore = true)
    @Mapping(target = "pickupLng", ignore = true)
    @Mapping(target = "dropoffLat", ignore = true)
    @Mapping(target = "dropoffLng", ignore = true)
    @Mapping(target = "requestedTime", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    RideRequest toEntity(RideRequestDTO rideRequestDTO);

    List<RideRequestDTO> toDtoList(List<RideRequest> rideRequests);

    List<RideRequest> toEntityList(List<RideRequestDTO> rideRequestDTOs);
}
