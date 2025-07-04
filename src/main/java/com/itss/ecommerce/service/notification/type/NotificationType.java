package com.itss.ecommerce.service.notification.type;

/**
 * Enum for different types of notifications
 */
public enum NotificationType {
    PAYMENT_NOTIFICATION("Payment Notification"),
    PASSWORD_UPDATE("Password Update"),
    ACCOUNT_STATUS_UPDATE("Account Status Update");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}