package com.ecober.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ecober.domain.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    List<Driver> findByDriverLocation(String driverLocation);

    @Query(value =
        "SELECT d.* FROM driver d " +
        "WHERE ST_DWithin(d.location, ST_Point(?1, ?2), ?3) " +
        "AND d.vehicle_type = ?4 " +
        "ORDER BY ST_Distance(d.location, ST_Point(?1, ?2)) " +
        "LIMIT 10",
        nativeQuery = true)
    List<Driver> findNearestDrivers(double lat, double lng, double radiusKm, String vehicleType);

    Optional<Driver> findByEmail(String email);

    Optional<Driver> findByDriverId(UUID driverId);
}
