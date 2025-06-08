package com.ecober.domain.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private final Map<String, String> lastNotifications = new ConcurrentHashMap<>();
    
    public void notifyRider(UUID riderId, String message) {
        String notification = String.format("[%s] Rider %s: %s", 
            LocalDateTime.now().toString(), riderId.toString(), message);
        lastNotifications.put("rider_" + riderId, notification);
        
        // In a real implementation, this would send push notifications, SMS, or emails
        System.out.println("RIDER NOTIFICATION: " + notification);
    }
    
    public void notifyDriver(String driverId, String message) {
        String notification = String.format("[%s] Driver %s: %s", 
            LocalDateTime.now().toString(), driverId, message);
        lastNotifications.put("driver_" + driverId, notification);
        
        // In a real implementation, this would send push notifications, SMS, or emails
        System.out.println("DRIVER NOTIFICATION: " + notification);
    }
    
    public void notifyBoth(UUID riderId, String driverId, String message) {
        notifyRider(riderId, message);
        notifyDriver(driverId, message);
    }
    
    public String getLastNotification(UUID userId, String userType) {
        return lastNotifications.get(userType.toLowerCase() + "_" + userId);
    }
    
    public void sendCarbonUpdateNotification(UUID userId, double co2Saved) {
        String message = String.format("Great job! You've saved %.2f kg of CO₂ with eco-friendly rides! 🌱", co2Saved);
        notifyRider(userId, message);
    }
    
    public void sendBadgeUpdateNotification(UUID userId, String newBadge) {
        String message = String.format("Congratulations! You've earned a new badge: %s 🏆", newBadge);
        notifyRider(userId, message);
    }
}
