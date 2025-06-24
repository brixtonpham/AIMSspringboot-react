package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.AuditLog;
import com.itss.ecommerce.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    
    /**
     * Log an action
     */
    public AuditLog logAction(String actionName, String note) {
        log.debug("Logging action: {} with note: {}", actionName, note);
        
        AuditLog auditLog = AuditLog.create(actionName, note);
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Log an action with user context
     */
    public AuditLog logAction(String actionName, String note, Long userId, AuditLog.ActionType actionType) {
        log.debug("Logging action: {} by user: {} with type: {}", actionName, userId, actionType);
        
        AuditLog auditLog = AuditLog.create(actionName, note, userId, actionType);
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Log entity operation
     */
    public AuditLog logAction(String actionName, String entityType, Long entityId, 
                            AuditLog.ActionType actionType, Long userId) {
        log.debug("Logging {} operation on {} with ID: {} by user: {}", 
                 actionType, entityType, entityId, userId);
        
        AuditLog auditLog = AuditLog.createForEntity(actionName, entityType, entityId, actionType, userId);
        return auditLogRepository.save(auditLog);
    }
    
    /**
     * Get all audit logs
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        log.debug("Fetching all audit logs");
        return auditLogRepository.findAllByOrderByRecordedAtDesc();
    }
    
    /**
     * Get logs by user
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByUser(Long userId) {
        log.debug("Fetching logs for user: {}", userId);
        return auditLogRepository.findByUserId(userId);
    }
    
    /**
     * Get logs by action type
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByActionType(AuditLog.ActionType actionType) {
        log.debug("Fetching logs by action type: {}", actionType);
        return auditLogRepository.findByActionType(actionType);
    }
    
    /**
     * Get logs for specific entity
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsForEntity(String entityType, Long entityId) {
        log.debug("Fetching logs for entity: {} with ID: {}", entityType, entityId);
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
    
    /**
     * Get recent logs (last 24 hours)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        log.debug("Fetching logs since: {}", cutoff);
        return auditLogRepository.findRecentLogs(cutoff);
    }
    
    /**
     * Get logs within date range
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching logs between {} and {}", startDate, endDate);
        return auditLogRepository.findLogsByDateRange(startDate, endDate);
    }
    
    /**
     * Search logs
     */
    @Transactional(readOnly = true)
    public List<AuditLog> searchLogs(String searchTerm) {
        log.debug("Searching logs with term: {}", searchTerm);
        return auditLogRepository.searchLogs(searchTerm);
    }
    
    /**
     * Get action statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getActionStatistics() {
        log.debug("Fetching action statistics");
        return auditLogRepository.getActionStatistics();
    }
    
    /**
     * Get user activity statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getUserActivityStatistics() {
        log.debug("Fetching user activity statistics");
        return auditLogRepository.getUserActivityStatistics();
    }
    
    /**
     * Get system logs (no user context)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getSystemLogs() {
        log.debug("Fetching system logs");
        return auditLogRepository.findSystemLogs();
    }
    
    /**
     * Get user logs (with user context)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getUserLogs() {
        log.debug("Fetching user logs");
        return auditLogRepository.findUserLogs();
    }
    
    /**
     * Count user actions in date range
     */
    @Transactional(readOnly = true)
    public long countUserActionsInDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Counting actions for user {} between {} and {}", userId, startDate, endDate);
        return auditLogRepository.countUserActionsInDateRange(userId, startDate, endDate);
    }
    
    /**
     * Get daily activity statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getDailyActivityStatistics(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        log.debug("Fetching daily activity statistics for last {} days", days);
        return auditLogRepository.getDailyActivityStatistics(startDate);
    }
    
    /**
     * Get critical actions (CREATE, UPDATE, DELETE)
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getCriticalActions() {
        log.debug("Fetching critical actions");
        return auditLogRepository.findCriticalActions();
    }
    
    /**
     * Log user login
     */
    public AuditLog logUserLogin(Long userId, String email) {
        return logAction(
            "User Login",
            "User logged in: " + email,
            userId,
            AuditLog.ActionType.LOGIN
        );
    }
    
    /**
     * Log user logout
     */
    public AuditLog logUserLogout(Long userId, String email) {
        return logAction(
            "User Logout",
            "User logged out: " + email,
            userId,
            AuditLog.ActionType.LOGOUT
        );
    }
    
    /**
     * Log payment action
     */
    public AuditLog logPayment(Long orderId, Long userId, String amount, String status) {
        return logAction(
            "Payment " + status,
            String.format("Payment of %s for order %d - Status: %s", amount, orderId, status),
            userId,
            AuditLog.ActionType.PAYMENT
        );
    }
    
    /**
     * Log order action
     */
    public AuditLog logOrderAction(Long orderId, Long userId, String action, String details) {
        return logAction(
            "Order " + action,
            "Order",
            orderId,
            AuditLog.ActionType.ORDER,
            userId
        );
    }
}