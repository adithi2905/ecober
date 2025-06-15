package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.RideRequestDTO;
import com.ecober.domain.model.RideRequest;
import com.ecober.domain.model.RideRequestStatus;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RideRequestMapper {

    RideRequestDTO toDto(RideRequest rideRequest);

    RideRequest toEntity(RideRequestDTO rideRequestDTO);

    List<RideRequestDTO> toDtoList(List<RideRequest> rideRequests);

    List<RideRequest> toEntityList(List<RideRequestDTO> rideRequestDTOs);

    default String map(RideRequestStatus status) {
        return status == null ? null : status.name();
    }

    default RideRequestStatus map(String status) {
        return status == null ? null : RideRequestStatus.valueOf(status);
    }
}
