package com.ecober.adapter.mapper;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.domain.model.Driver;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-06-03T19:49:41-0400",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.42.0.v20250514-1000, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class DriverMapperImpl implements DriverMapper {

    @Override
    public DriverDTO toDto(Driver driver) {
        if ( driver == null ) {
            return null;
        }

        DriverDTO.DriverDTOBuilder driverDTO = DriverDTO.builder();

        driverDTO.driverId( driver.getDriverId() );
        driverDTO.driverLocation( driver.getDriverLocation() );
        driverDTO.driverName( driver.getDriverName() );
        driverDTO.fuelEfficiency( driver.getFuelEfficiency() );
        driverDTO.totalCO2Saved( driver.getTotalCO2Saved() );
        driverDTO.trustScore( driver.getTrustScore() );
        driverDTO.vehicleNo( driver.getVehicleNo() );
        driverDTO.vehicleType( driver.getVehicleType() );
        driverDTO.verifiedDriver( driver.isVerifiedDriver() );

        return driverDTO.build();
    }

    @Override
    public Driver toEntity(DriverDTO driverDTO) {
        if ( driverDTO == null ) {
            return null;
        }

        Driver driver = new Driver();

        driver.setDriverId( driverDTO.getDriverId() );
        driver.setDriverLocation( driverDTO.getDriverLocation() );
        driver.setDriverName( driverDTO.getDriverName() );
        driver.setFuelEfficiency( driverDTO.getFuelEfficiency() );
        driver.setTotalCO2Saved( driverDTO.getTotalCO2Saved() );
        driver.setTrustScore( driverDTO.getTrustScore() );
        driver.setVehicleNo( driverDTO.getVehicleNo() );
        driver.setVehicleType( driverDTO.getVehicleType() );
        driver.setVerifiedDriver( driverDTO.isVerifiedDriver() );

        return driver;
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
