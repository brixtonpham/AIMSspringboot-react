package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_operation_tracker",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "operation_date"}))
@Data
public class DailyOperationTracker {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "operation_date", nullable = false)
    private LocalDate operationDate;
    
    @Column(name = "operation_count", nullable = false)
    private Integer operationCount = 0;
    
    @Column(name = "is_editing", nullable = false)
    private Boolean isEditing = false;
    
    @Column(name = "last_edit_started")
    private LocalDateTime lastEditStarted;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}