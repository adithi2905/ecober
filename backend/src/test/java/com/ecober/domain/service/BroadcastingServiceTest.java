package com.ecober.domain.service;

import com.ecober.adapter.Dto.DriverDTO;
import com.ecober.adapter.mapper.DriverMapper;
import com.ecober.domain.model.Driver;
import com.ecober.infrastructure.repository.DriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BroadcastingServiceTest {

    private DriverRepository driverRepository;
    private GeocodingService geocodingService;
    private DriverMapper driverMapper;
    private BroadcastingService broadcastingService;

    @BeforeEach
    void setUp() {
        driverRepository = mock(DriverRepository.class);
        geocodingService = mock(GeocodingService.class);
        driverMapper = mock(DriverMapper.class);

        broadcastingService = new BroadcastingService();
        inject(broadcastingService, "driverRepository", driverRepository);
        inject(broadcastingService, "geocodingService", geocodingService);
        inject(broadcastingService, "driverMapper", driverMapper);
    }

    private void inject(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFindAndNotifyTopDrivers() {
        Driver driver1 = new Driver(); driver1.setDriverLocation("Loc1"); driver1.setVehicleType("EV");
        Driver driver2 = new Driver(); driver2.setDriverLocation("Loc2"); driver2.setVehicleType("EV");
        Driver driver3 = new Driver(); driver3.setDriverLocation("Loc3"); driver3.setVehicleType("SEDAN");

        when(driverRepository.findAll()).thenReturn(List.of(driver1, driver2, driver3));
        when(geocodingService.getLatAndLong("Loc1")).thenReturn(new double[]{10.0, 10.0});
        when(geocodingService.getLatAndLong("Loc2")).thenReturn(new double[]{20.0, 20.0});

        DriverDTO dto1 = new DriverDTO(); dto1.setDriverName("Driver 1");
        DriverDTO dto2 = new DriverDTO(); dto2.setDriverName("Driver 2");

        when(driverMapper.toDto(driver1)).thenReturn(dto1);
        when(driverMapper.toDto(driver2)).thenReturn(dto2);

        List<DriverDTO> result = broadcastingService.findAndNotifyTopDrivers(12.0, 12.0, "EV", 2);

        assertEquals(2, result.size());
        assertEquals("Driver 1", result.get(0).getDriverName()); // closer driver
        assertEquals("Driver 2", result.get(1).getDriverName());
    }
}
