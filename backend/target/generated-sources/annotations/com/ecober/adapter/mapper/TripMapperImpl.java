package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.TripDTO;
import com.ecober.domain.model.Trip;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-10T15:28:53-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class TripMapperImpl implements TripMapper {

    @Override
    public TripDTO toDto(Trip trip) {
        if ( trip == null ) {
            return null;
        }

        TripDTO.TripDTOBuilder tripDTO = TripDTO.builder();

        tripDTO.carbonEmissions( trip.getCarbonEmissions() );
        tripDTO.driverId( trip.getDriverId() );
        tripDTO.driverName( trip.getDriverName() );
        tripDTO.ecoScore( trip.getEcoScore() );
        tripDTO.endTime( trip.getEndTime() );
        tripDTO.estimatedEmission( trip.getEstimatedEmission() );
        tripDTO.feedback( trip.getFeedback() );
        tripDTO.startTime( trip.getStartTime() );
        tripDTO.status( trip.getStatus() );

        return tripDTO.build();
    }

    @Override
    public Trip toEntity(TripDTO tripDTO) {
        if ( tripDTO == null ) {
            return null;
        }

        Trip trip = new Trip();

        trip.setCarbonEmissions( tripDTO.getCarbonEmissions() );
        trip.setDriverId( tripDTO.getDriverId() );
        trip.setDriverName( tripDTO.getDriverName() );
        trip.setEcoScore( tripDTO.getEcoScore() );
        trip.setEndTime( tripDTO.getEndTime() );
        trip.setEstimatedEmission( tripDTO.getEstimatedEmission() );
        trip.setFeedback( tripDTO.getFeedback() );
        trip.setStartTime( tripDTO.getStartTime() );
        trip.setStatus( tripDTO.getStatus() );

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
