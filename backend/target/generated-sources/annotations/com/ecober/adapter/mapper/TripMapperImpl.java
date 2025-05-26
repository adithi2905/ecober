package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Trip;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-21T21:41:23-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public TripDTO toDto(Trip results) {
        if ( results == null ) {
            return null;
        }

        TripDTO.TripDTOBuilder tripDTO = TripDTO.builder();

        tripDTO.userId( results.getUserId() );
        tripDTO.driverId( results.getDriverId() );
        tripDTO.startTime( results.getStartTime() );
        tripDTO.endTime( results.getEndTime() );
        tripDTO.carbonEmissions( results.getCarbonEmissions() );
        tripDTO.route( results.getRoute() );

        return tripDTO.build();
    }

    @Override
    public Trip toEntity(TripDTO tripDTO) {
        if ( tripDTO == null ) {
            return null;
        }

        Trip trip = new Trip();

        return trip;
    }

    @Override
    public List<TripDTO> toDtoList(List<Trip> trips) {
        if ( trips == null ) {
            return null;
        }

        List<TripDTO> list = new ArrayList<TripDTO>( trips.size() );
        for ( Trip trip : trips ) {
            list.add( toDto( trip ) );
        }

        return list;
    }

    @Override
    public List<Trip> toEntityList(List<TripDTO> tripsDTO) {
        if ( tripsDTO == null ) {
            return null;
        }

        List<Trip> list = new ArrayList<Trip>( tripsDTO.size() );
        for ( TripDTO tripDTO : tripsDTO ) {
            list.add( toEntity( tripDTO ) );
        }

        return list;
    }
}
