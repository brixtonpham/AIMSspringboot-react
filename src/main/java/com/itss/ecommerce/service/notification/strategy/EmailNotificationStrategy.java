package com.itss.ecommerce.service.notification.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.itss.ecommerce.service.notification.type.Notification;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;

/**
 * Email implementation of NotificationProvider
 */
@Component
public class EmailNotificationStrategy implements INotificationStrategy {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public boolean send(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getContent());
            
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + notification.getRecipient());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + notification.getRecipient() + " - Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public NotificationServiceProvider getProvider() {
        return NotificationServiceProvider.EMAIL;
    }
    
    @Override
    public boolean isAvailable() {
        return mailSender != null;
    }
}