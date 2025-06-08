package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Trip;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-07T21:59:56-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public TripDTO toDto(Trip trip) {
        if ( trip == null ) {
            return null;
        }

        TripDTO.TripDTOBuilder tripDTO = TripDTO.builder();

        tripDTO.driverId( trip.getDriverId() );
        tripDTO.startTime( trip.getStartTime() );
        tripDTO.endTime( trip.getEndTime() );
        tripDTO.carbonEmissions( trip.getCarbonEmissions() );
        tripDTO.driverName( trip.getDriverName() );
        tripDTO.estimatedEmission( trip.getEstimatedEmission() );
        tripDTO.ecoScore( trip.getEcoScore() );
        tripDTO.feedback( trip.getFeedback() );

        return tripDTO.build();
    }

    @Override
    public Trip toEntity(TripDTO tripDTO) {
        if ( tripDTO == null ) {
            return null;
        }

        Trip trip = new Trip();

        trip.setDriverId( tripDTO.getDriverId() );
        trip.setDriverName( tripDTO.getDriverName() );
        trip.setEstimatedEmission( tripDTO.getEstimatedEmission() );
        trip.setEcoScore( tripDTO.getEcoScore() );
        trip.setFeedback( tripDTO.getFeedback() );
        trip.setStartTime( tripDTO.getStartTime() );
        trip.setEndTime( tripDTO.getEndTime() );
        trip.setCarbonEmissions( tripDTO.getCarbonEmissions() );

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
