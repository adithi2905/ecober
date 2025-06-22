package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.domain.model.Driver;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-21T20:46:33-0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class DriverMapperImpl implements DriverMapper {

    @Override
    public DriverDTO toDto(Driver driver) {
        if ( driver == null ) {
            return null;
        }

        DriverDTO.DriverDTOBuilder driverDTO = DriverDTO.builder();

        driverDTO.driverName( driver.getDriverName() );
        driverDTO.password( driver.getPassword() );
        driverDTO.vehicleNo( driver.getVehicleNo() );
        driverDTO.driverId( driver.getDriverId() );
        driverDTO.verifiedDriver( driver.isVerifiedDriver() );
        driverDTO.driverLocation( driver.getDriverLocation() );
        driverDTO.vehicleType( driver.getVehicleType() );
        driverDTO.fuelEfficiency( driver.getFuelEfficiency() );
        driverDTO.trustScore( driver.getTrustScore() );
        driverDTO.totalCO2Saved( driver.getTotalCO2Saved() );

        return driverDTO.build();
    }

    @Override
    public Driver toEntity(DriverDTO driverDTO) {
        if ( driverDTO == null ) {
            return null;
        }

        Driver.DriverBuilder driver = Driver.builder();

        driver.driverId( driverDTO.getDriverId() );
        driver.password( driverDTO.getPassword() );
        driver.driverName( driverDTO.getDriverName() );
        driver.vehicleNo( driverDTO.getVehicleNo() );
        driver.verifiedDriver( driverDTO.isVerifiedDriver() );
        driver.driverLocation( driverDTO.getDriverLocation() );
        driver.vehicleType( driverDTO.getVehicleType() );
        driver.fuelEfficiency( driverDTO.getFuelEfficiency() );
        driver.trustScore( driverDTO.getTrustScore() );
        driver.totalCO2Saved( driverDTO.getTotalCO2Saved() );

        return driver.build();
    }

    @Override
    public List<DriverDTO> toDtoList(List<Driver> drivers) {
        if ( drivers == null ) {
            return null;
        }

        List<DriverDTO> list = new ArrayList<DriverDTO>( drivers.size() );
        for ( Driver driver : drivers ) {
            list.add( toDto( driver ) );
        }

        return list;
    }

    @Override
    public List<Driver> toEntityList(List<DriverDTO> driverDTOs) {
        if ( driverDTOs == null ) {
            return null;
        }

        List<Driver> list = new ArrayList<Driver>( driverDTOs.size() );
        for ( DriverDTO driverDTO : driverDTOs ) {
            list.add( toEntity( driverDTO ) );
        }

        return list;
    }
}
