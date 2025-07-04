package com.itss.ecommerce.service.notification.type;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a notification message with recipient and content
 */
public class Notification {

    private String recipient;
    private String subject;
    private String content;
    private NotificationType type;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;

    public Notification(String recipient, String subject, String content, NotificationType type) {
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}