package com.itss.ecommerce.service.notification.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itss.ecommerce.service.notification.type.Notification;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;

/**
 * SMS implementation of NotificationProvider
 * This is a placeholder implementation - integrate with actual SMS service
 */
@Component
public class SMSNotificationStrategy implements INotificationStrategy {
    
    @Value("${notification.sms.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${notification.sms.api-key:}")
    private String apiKey;
    
    @Override
    public boolean send(Notification notification) {
        if (!isAvailable()) {
            System.err.println("SMS service is not configured or available");
            return false;
        }
        
        try {
            // TODO: Integrate with actual SMS service (Twilio, AWS SNS, etc.)
            // Example implementation structure:
            // 1. Validate phone number format
            // 2. Call SMS API with apiKey
            // 3. Handle response and errors
            
            System.out.println("SMS would be sent to: " + notification.getRecipient());
            System.out.println("Content: " + notification.getContent());
            
            // Simulate SMS sending
            Thread.sleep(100);
            
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send SMS to: " + notification.getRecipient() + " - Error: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public NotificationServiceProvider getProvider() {
        return NotificationServiceProvider.SMS;
    }
    
    @Override
    public boolean isAvailable() {
        return smsEnabled && apiKey != null && !apiKey.trim().isEmpty();
    }
}