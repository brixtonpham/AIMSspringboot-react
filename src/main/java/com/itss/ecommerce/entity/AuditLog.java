package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "logger")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id")
    private Long actionId;
    
    @Column(name = "action_name", nullable = false, length = 255)
    private String actionName;
    
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "entity_type", length = 50)
    private String entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action_type")
    private ActionType actionType;
    
    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
    
    public enum ActionType {
        CREATE, UPDATE, DELETE, VIEW, LOGIN, LOGOUT, PAYMENT, ORDER, CANCEL
    }
    
    /**
     * Create audit log entry
     */
    public static AuditLog create(String actionName, String note) {
        AuditLog log = new AuditLog();
        log.setActionName(actionName);
        log.setNote(note);
        log.setRecordedAt(LocalDateTime.now());
        return log;
    }
    
    /**
     * Create audit log with user context
     */
    public static AuditLog create(String actionName, String note, Long userId, ActionType actionType) {
        AuditLog log = create(actionName, note);
        log.setUserId(userId);
        log.setActionType(actionType);
        return log;
    }
    
    /**
     * Create audit log for entity operation
     */
    public static AuditLog createForEntity(String actionName, String entityType, Long entityId, 
                                         ActionType actionType, Long userId) {
        AuditLog log = new AuditLog();
        log.setActionName(actionName);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setActionType(actionType);
        log.setUserId(userId);
        log.setNote(String.format("%s %s with ID: %d", actionType.name().toLowerCase(), 
                                entityType.toLowerCase(), entityId));
        log.setRecordedAt(LocalDateTime.now());
        return log;
    }
    
    /**
     * Get formatted timestamp
     */
    public String getFormattedTimestamp() {
        if (recordedAt == null) return "Unknown";
        return recordedAt.toString();
    }
}