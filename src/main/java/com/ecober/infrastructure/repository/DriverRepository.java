package com.ecober.infrastructure.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecober.domain.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver,String>{
    List<Driver> findByDriverLocation(String driverLocation);

}
