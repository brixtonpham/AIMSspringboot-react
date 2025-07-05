package com.itss.ecommerce.service.notification.utils;

import com.itss.ecommerce.entity.Order;
import com.itss.ecommerce.entity.OrderItem;
import com.itss.ecommerce.entity.User;

/**
 * Builder class for creating notification messages
 */
public class NotificationMessageBuilder {
    
    /**
     * Builds payment notification message (Invoice of Order)
     */
    public static String buildPaymentNotificationMessage(Order order) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(order.getDeliveryInformation().getName()).append(",\n\n");
        message.append("Thank you for your payment! Here is your invoice:\n\n");
        
        message.append("INVOICE\n");
        message.append("=======\n");
        message.append("Order ID: ").append(order.getOrderId()).append("\n");
        message.append("Date: ").append(order.getCreatedAt()).append("\n");
        message.append("Payment Status: PAID\n\n");
        
        message.append("BILLING INFORMATION:\n");
        message.append("Name: ").append(order.getDeliveryInformation().getName()).append("\n");
        message.append("Email: ").append(order.getDeliveryInformation().getEmail()).append("\n");
        message.append("Phone: ").append(order.getDeliveryInformation().getPhone()).append("\n");
        message.append("Address: ").append(order.getDeliveryInformation().getAddress()).append("\n\n");
        
        message.append("ITEMS PURCHASED:\n");
        message.append("----------------------------------------------------------------\n");
        message.append("Item\t\t\tQty\tUnit Price\tTotal\n");
        message.append("----------------------------------------------------------------\n");
        
        for (OrderItem item : order.getOrderItems()) {
            message.append(item.getProduct().getTitle()).append("\t");
            message.append(item.getQuantity()).append("\t");
            message.append("$").append(item.getUnitPrice()).append("\t\t");
            message.append("$").append(item.getUnitPrice() * item.getQuantity()).append("\n");
        }
        
        message.append("----------------------------------------------------------------\n");
        message.append("Subtotal: $").append(order.getTotalBeforeVat()).append("\n");
        message.append("VAT: $").append(order.getTotalAfterVat() - order.getTotalBeforeVat()).append("\n");
        message.append("TOTAL: $").append(order.getTotalAfterVat()).append("\n\n");
        
        message.append("Your order will be processed and shipped within 2-3 business days.\n");
        message.append("You will receive a tracking number once your order is shipped.\n\n");
        
        message.append("Thank you for your business!\n");
        message.append("Best regards,\nITSS E-commerce Team");
        
        return message.toString();
    }
    
    /**
     * Builds password update notification message
     */
    public static String buildPasswordUpdateMessage(User user) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(user.getName()).append(",\n\n");
        message.append("Your password has been successfully updated.\n\n");
        
        message.append("Account Details:\n");
        message.append("Email: ").append(user.getEmail()).append("\n");
        message.append("Account ID: ").append(user.getUserId()).append("\n");
        message.append("Updated: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        message.append("Security Tips:\n");
        message.append("- Keep your password secure and don't share it with anyone\n");
        message.append("- Use a strong, unique password for your account\n");
        message.append("- Log out from public computers after use\n\n");
        
        message.append("If you did not make this change, please contact our support team immediately.\n\n");
        
        message.append("Contact Information:\n");
        message.append("Email: support@itss-ecommerce.com\n");
        message.append("Phone: +84 123 456 789\n\n");
        
        message.append("Best regards,\n");
        message.append("ITSS E-commerce Security Team");
        
        return message.toString();
    }
    
    /**
     * Builds account status update message (blocked)
     */
    public static String buildAccountBlockedMessage(User user, String reason, String blockedBy) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(user.getName()).append(",\n\n");
        message.append("We are writing to inform you that your account access has been temporarily suspended.\n\n");
        message.append("Account Details:\n");
        message.append("Email: ").append(user.getEmail()).append("\n");
        message.append("Account ID: ").append(user.getUserId()).append("\n\n");
        
        if (reason != null && !reason.trim().isEmpty()) {
            message.append("Reason for suspension: ").append(reason).append("\n\n");
        }
        
        message.append("What this means:\n");
        message.append("- You will not be able to log into your account\n");
        message.append("- You cannot place new orders\n");
        message.append("- Your existing orders will continue to be processed\n\n");
        
        message.append("If you believe this is an error or would like to appeal this decision, ");
        message.append("please contact our customer support team.\n\n");
        
        message.append("Contact Information:\n");
        message.append("Email: support@itss-ecommerce.com\n");
        message.append("Phone: +84 123 456 789\n\n");
        
        message.append("We appreciate your understanding.\n\n");
        message.append("Best regards,\n");
        message.append("ITSS E-commerce Administration Team");
        
        return message.toString();
    }
    
    /**
     * Builds account status update message (unblocked)
     */
    public static String buildAccountUnblockedMessage(User user, String unblockedBy) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(user.getName()).append(",\n\n");
        message.append("Good news! Your account access has been restored.\n\n");
        message.append("Account Details:\n");
        message.append("Email: ").append(user.getEmail()).append("\n");
        message.append("Account ID: ").append(user.getUserId()).append("\n\n");
        
        message.append("You can now:\n");
        message.append("- Log into your account\n");
        message.append("- Browse and purchase products\n");
        message.append("- Access your order history\n");
        message.append("- Use all account features\n\n");
        
        message.append("We encourage you to review our terms of service to ensure ");
        message.append("continued compliance with our policies.\n\n");
        
        message.append("If you have any questions or need assistance, please don't hesitate ");
        message.append("to contact our customer support team.\n\n");
        
        message.append("Contact Information:\n");
        message.append("Email: support@itss-ecommerce.com\n");
        message.append("Phone: +84 123 456 789\n\n");
        
        message.append("Thank you for your patience and welcome back!\n\n");
        message.append("Best regards,\n");
        message.append("ITSS E-commerce Administration Team");
        
        return message.toString();
    }
    
    /**
     * Builds user registration welcome message
     */
    public static String buildUserRegistrationMessage(User user) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(user.getName()).append(",\n\n");
        message.append("Welcome to ITSS E-commerce! Your account has been created successfully.\n\n");
        
        message.append("Account Details:\n");
        message.append("Email: ").append(user.getEmail()).append("\n");
        message.append("Account ID: ").append(user.getUserId()).append("\n");
        message.append("Role: ").append(user.getRole()).append("\n");
        message.append("Created: ").append(java.time.LocalDateTime.now()).append("\n\n");
        
        message.append("What you can do with your account:\n");
        if (user.getRole() == User.UserRole.ADMIN) {
            message.append("- Full administrative access to the system\n");
            message.append("- Manage users, products, and orders\n");
            message.append("- View system analytics and reports\n");
            message.append("- Configure system settings\n");
        } else if (user.getRole() == User.UserRole.MANAGER) {
            message.append("- Manage products and inventory\n");
            message.append("- Process and manage orders\n");
            message.append("- View sales reports\n");
            message.append("- Assist customers with their queries\n");
        } else {
            message.append("- Browse our extensive product catalog\n");
            message.append("- Place orders and track deliveries\n");
            message.append("- Manage your profile and preferences\n");
            message.append("- Access exclusive member offers\n");
        }
        message.append("\n");
        
        message.append("Getting Started:\n");
        message.append("1. Log in to your account using your email and password\n");
        message.append("2. Complete your profile information\n");
        message.append("3. Explore our features and start using the platform\n\n");
        
        message.append("Security Reminder:\n");
        message.append("- Keep your login credentials secure\n");
        message.append("- Use a strong password for your account\n");
        message.append("- Log out when using shared computers\n\n");
        
        message.append("Need Help?\n");
        message.append("If you have any questions or need assistance, our support team is here to help.\n\n");
        
        message.append("Contact Information:\n");
        message.append("Email: support@itss-ecommerce.com\n");
        message.append("Phone: +84 123 456 789\n\n");
        
        message.append("Thank you for joining ITSS E-commerce!\n\n");
        message.append("Best regards,\n");
        message.append("ITSS E-commerce Team");
        
        return message.toString();
    }
}