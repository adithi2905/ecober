package com.ecober.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class NotificationServiceTest {

    private NotificationService notificationService;
    private UUID riderId;
    private UUID driverId;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService();
        riderId = UUID.randomUUID();
        driverId = UUID.randomUUID();
    }

    @Test
    void testNotifyRiderStoresMessage() {
        notificationService.notifyRider(riderId, "Your ride is on the way!");
        String lastNotification = notificationService.getLastNotification(riderId, "rider");

        assertThat(lastNotification).contains("Rider");
        assertThat(lastNotification).contains(riderId.toString());
        assertThat(lastNotification).contains("Your ride is on the way!");
    }

    @Test
    void testNotifyDriverStoresMessage() {
        notificationService.notifyDriver(driverId, "New trip assigned.");
        String lastNotification = notificationService.getLastNotification(driverId, "driver");

        assertThat(lastNotification).contains("Driver");
        assertThat(lastNotification).contains(driverId.toString());
        assertThat(lastNotification).contains("New trip assigned.");
    }

    @Test
    void testNotifyBoth() {
        notificationService.notifyBoth(riderId, driverId, "Trip confirmed.");

        String riderNotification = notificationService.getLastNotification(riderId, "rider");
        String driverNotification = notificationService.getLastNotification(driverId, "driver");

        assertThat(riderNotification).contains("Trip confirmed.");
        assertThat(driverNotification).contains("Trip confirmed.");
    }

    @Test
    void testSendCarbonUpdateNotification() {
        notificationService.sendCarbonUpdateNotification(riderId, 12.34);
        String msg = notificationService.getLastNotification(riderId, "rider");
        assertThat(msg).contains("12.34").contains("CO₂");
    }

    @Test
    void testSendBadgeUpdateNotification() {
        notificationService.sendBadgeUpdateNotification(riderId, "Eco Warrior");
        String msg = notificationService.getLastNotification(riderId, "rider");
        assertThat(msg).contains("Eco Warrior").contains("Congratulations");
    }
}
