package com.ecober.infrastructure.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ecober.domain.model.Route;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RouteRepository extends JpaRepository<Route,String>{

    Optional<Route> findBySourceAndDestination(String riderPickupLocation, String riderDropOffLocation);


@Query("SELECT r FROM Route r WHERE " +
       "ABS(r.source.latitude - :pickupLat) < 0.0001 AND " +
       "ABS(r.source.longitude - :pickupLng) < 0.0001 AND " +
       "ABS(r.destination.latitude - :dropoffLat) < 0.0001 AND " +
       "ABS(r.destination.longitude - :dropoffLng) < 0.0001")
Optional<Route> findByCoordinates(
        @Param("pickupLat") double pickupLat,
        @Param("pickupLng") double pickupLng,
        @Param("dropoffLat") double dropoffLat,
        @Param("dropoffLng") double dropoffLng
);
    
}
