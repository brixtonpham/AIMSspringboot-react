package com.itss.ecommerce.service.notification.type;

/**
 * Enum for different notification provider types
 */
public enum NotificationServiceProvider {
    EMAIL("Email"),
    SMS("SMS"),
    ZALO("Zalo"),
    PUSH("Push Notification"),
    SLACK("Slack"),
    TELEGRAM("Telegram");
    
    private final String displayName;
    
    NotificationServiceProvider(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}