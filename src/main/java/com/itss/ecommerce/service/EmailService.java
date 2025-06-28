package com.itss.ecommerce.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.itss.ecommerce.entity.OrderItem;
import com.itss.ecommerce.entity.Order;
import com.itss.ecommerce.entity.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderConfirmationEmail(Order order) {
        if (order == null) {
            System.err.println("Cannot send email: Order is null");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(order.getDeliveryInformation().getEmail());
            System.out.println("Sending email to: " + order.getDeliveryInformation().getEmail());

            message.setSubject("Order Confirmation - Order #" + order.getOrderId());
            message.setText(buildOrderConfirmationMessage(order));
            mailSender.send(message);
            
            System.out.println("Email sent successfully to: " + order.getDeliveryInformation().getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + order.getDeliveryInformation().getEmail() + " - Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildOrderConfirmationMessage(Order order) {
        StringBuilder message = new StringBuilder();
        message.append("Dear ").append(order.getDeliveryInformation().getName()).append(",\n\n");
        message.append("Thank you for your order! Your order has been confirmed.\n\n");
        message.append("Order Details:\n");
        message.append("Order ID: ").append(order.getOrderId()).append("\n");
        message.append("Total Amount: $").append(order.getTotalAfterVat()).append("\n\n");
        
        message.append("Items Ordered:\n");
        for (OrderItem item : order.getOrderItems()) {
            message.append("- ").append(item.getProduct().getTitle())
                   .append(" (Quantity: ").append(item.getQuantity()).append(")")
                   .append(" - $").append(item.getUnitPrice()).append("\n");
        }
        
        message.append("\nDelivery Information:\n");
        message.append("Name: ").append(order.getDeliveryInformation().getName()).append("\n");
        message.append("Phone: ").append(order.getDeliveryInformation().getPhone()).append("\n");
        message.append("Address: ").append(order.getDeliveryInformation().getAddress()).append("\n");
        
        message.append("\nWe will notify you when your order is shipped.\n\n");
        message.append("Thank you for shopping with us!\n");
        message.append("Best regards,\nITSS E-commerce Team");
        
        //message.append("http://localhost:8080/order-confirmation/").append(order.getOrderId()).append("\n");
        return message.toString();
    }

    public void sendUserBlockedEmail(User user, String reason, String blockedBy) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            System.out.println("Sending user blocked email to: " + user.getEmail());

            message.setSubject("Account Access Suspended - ITSS E-commerce");
            message.setText(buildUserBlockedMessage(user, reason, blockedBy));
            mailSender.send(message);
            
            System.out.println("User blocked email sent successfully to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send user blocked email to: " + user.getEmail() + " - Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendUserUnblockedEmail(User user, String unblockedBy) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            System.out.println("Sending user unblocked email to: " + user.getEmail());

            message.setSubject("Account Access Restored - ITSS E-commerce");
            message.setText(buildUserUnblockedMessage(user, unblockedBy));
            mailSender.send(message);
            
            System.out.println("User unblocked email sent successfully to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send user unblocked email to: " + user.getEmail() + " - Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildUserBlockedMessage(User user, String reason, String blockedBy) {
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

    private String buildUserUnblockedMessage(User user, String unblockedBy) {
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
}