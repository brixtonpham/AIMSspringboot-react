package com.itss.ecommerce.service.notification.strategy;

import com.itss.ecommerce.service.notification.type.Notification;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;

/**
 * Base interface for all notification providers (Email, SMS, Zalo, etc.)
 */
public interface INotificationStrategy {
    
    /**
     * Sends a notification through this provider
     * 
     * @param notification the notification to send
     * @return true if sent successfully, false otherwise
     */
    boolean send(Notification notification);
    
    /**
     * Gets the provider type
     * 
     * @return the notification provider type
     */
    NotificationServiceProvider getProvider();
    
    /**
     * Checks if this provider is available/configured
     * 
     * @return true if provider is available, false otherwise
     */
    boolean isAvailable();
}