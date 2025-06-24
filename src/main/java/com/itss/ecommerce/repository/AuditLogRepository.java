package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Find audit logs by action type
     */
    List<AuditLog> findByActionType(AuditLog.ActionType actionType);
    
    /**
     * Find audit logs by user ID
     */
    List<AuditLog> findByUserId(Long userId);
    
    /**
     * Find audit logs by entity type
     */
    List<AuditLog> findByEntityType(String entityType);
    
    /**
     * Find audit logs by entity type and entity ID
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    /**
     * Find audit logs within date range
     */
    @Query("SELECT a FROM AuditLog a WHERE a.recordedAt BETWEEN :startDate AND :endDate")
    List<AuditLog> findLogsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find recent audit logs (last 24 hours)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.recordedAt >= :cutoffDate ORDER BY a.recordedAt DESC")
    List<AuditLog> findRecentLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find audit logs by action name containing
     */
    @Query("SELECT a FROM AuditLog a WHERE LOWER(a.actionName) LIKE LOWER(CONCAT('%', :action, '%'))")
    List<AuditLog> findByActionNameContaining(@Param("action") String action);
    
    /**
     * Count actions by user in date range
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.userId = :userId AND a.recordedAt BETWEEN :startDate AND :endDate")
    long countUserActionsInDateRange(@Param("userId") Long userId, 
                                   @Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get action statistics by type
     */
    @Query("SELECT a.actionType, COUNT(a) FROM AuditLog a GROUP BY a.actionType ORDER BY COUNT(a) DESC")
    List<Object[]> getActionStatistics();
    
    /**
     * Get user activity statistics
     */
    @Query("SELECT a.userId, COUNT(a), MAX(a.recordedAt) FROM AuditLog a WHERE a.userId IS NOT NULL GROUP BY a.userId ORDER BY COUNT(a) DESC")
    List<Object[]> getUserActivityStatistics();
    
    /**
     * Find all logs ordered by timestamp (newest first)
     */
    List<AuditLog> findAllByOrderByRecordedAtDesc();
    
    /**
     * Find system logs (no user ID)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId IS NULL ORDER BY a.recordedAt DESC")
    List<AuditLog> findSystemLogs();
    
    /**
     * Find user logs (with user ID)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId IS NOT NULL ORDER BY a.recordedAt DESC")
    List<AuditLog> findUserLogs();
    
    /**
     * Search logs by action name or note
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
           "LOWER(a.actionName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.note) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<AuditLog> searchLogs(@Param("search") String searchTerm);
    
    /**
     * Get daily activity statistics
     */
    @Query("SELECT DATE(a.recordedAt), COUNT(a), COUNT(DISTINCT a.userId) FROM AuditLog a WHERE a.recordedAt >= :startDate GROUP BY DATE(a.recordedAt) ORDER BY DATE(a.recordedAt)")
    List<Object[]> getDailyActivityStatistics(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Find critical actions (CREATE, UPDATE, DELETE)
     */
    @Query("SELECT a FROM AuditLog a WHERE a.actionType IN ('CREATE', 'UPDATE', 'DELETE') ORDER BY a.recordedAt DESC")
    List<AuditLog> findCriticalActions();
}