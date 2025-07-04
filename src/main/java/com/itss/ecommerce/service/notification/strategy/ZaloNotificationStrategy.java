package com.itss.ecommerce.service.notification.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itss.ecommerce.service.notification.type.Notification;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;

/**
 * Zalo implementation of NotificationProvider
 * This is a placeholder implementation - integrate with Zalo OA API
 */
@Component
public class ZaloNotificationStrategy implements INotificationStrategy {
    
    @Value("${notification.zalo.enabled:false}")
    private boolean zaloEnabled;
    
    @Value("${notification.zalo.oa-id:}")
    private String oaId;
    
    @Value("${notification.zalo.access-token:}")
    private String accessToken;
    
    @Override
    public boolean send(Notification notification) {
        if (!isAvailable()) {
            System.err.println("Zalo service is not configured or available");
            return false;
        }
        
        try {
            // TODO: Integrate with Zalo Official Account API
            // Example implementation structure:
            // 1. Prepare Zalo message format
            // 2. Call Zalo API with access token
            // 3. Handle response and errors
            
            System.out.println("Zalo message would be sent to: " + notification.getRecipient());
            System.out.println("Content: " + notification.getContent());
            
            // Simulate Zalo sending
            Thread.sleep(150);
            
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send Zalo message to: " + notification.getRecipient() + " - Error: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public NotificationServiceProvider getProvider() {
        return NotificationServiceProvider.ZALO;
    }
    
    @Override
    public boolean isAvailable() {
        return zaloEnabled && oaId != null && !oaId.trim().isEmpty() 
               && accessToken != null && !accessToken.trim().isEmpty();
    }
}