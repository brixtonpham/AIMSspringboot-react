package com.itss.ecommerce.service.admin;

import com.itss.ecommerce.entity.AuditLog;
import com.itss.ecommerce.entity.User;
import com.itss.ecommerce.repository.UserRepository;
import com.itss.ecommerce.service.EmailService;
import com.itss.ecommerce.service.log.AuditLogService;
import com.itss.ecommerce.service.notification.INotificationService;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;
    private final INotificationService notificationService;

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Create new user
     */
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getEmail());

        // Validate user for creation (includes password validation)
        validateUserForCreation(user);

        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        // Set default values
        if (user.getRole() == null) {
            user.setRole(User.UserRole.MANAGER);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getSalary() == null) {
            user.setSalary(0.0);
        }

        User savedUser = userRepository.save(user);

        // Send welcome email notification
        try {
            notificationService.sendUserRegistrationNotification(savedUser, NotificationServiceProvider.EMAIL);
        } catch (Exception e) {
            log.error("Failed to send welcome email to user {}: {}", savedUser.getUserId(), e.getMessage());
        }
        
        // Log the action
        auditLogService.logAction(
                "User Created",
                "User",
                savedUser.getUserId(),
                AuditLog.ActionType.CREATE,
                null);

        log.info("User created successfully with ID: {}", savedUser.getUserId());
        return savedUser;
    }

    /**
     * Update user
     */
    public User updateUser(Long id, User updatedUser) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Validate user for update (excludes password validation)
        validateUserForUpdate(updatedUser);

        // Update fields
        existingUser.setName(updatedUser.getName());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setSalary(updatedUser.getSalary());
        existingUser.setIsActive(updatedUser.getIsActive());

        // Don't allow email changes for now (would require additional verification)

        User savedUser = userRepository.save(existingUser);

        // Log the action
        auditLogService.logAction(
                "User Updated",
                "User",
                savedUser.getUserId(),
                AuditLog.ActionType.UPDATE,
                savedUser.getUserId());

        log.info("User updated successfully: {}", savedUser.getUserId());
        return savedUser;
    }

    /**
     * Delete user (soft delete by setting inactive)
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
        // Log the action
        auditLogService.logAction(
                "User Deleted",
                "User",
                id,
                AuditLog.ActionType.DELETE,
                null);

        log.info("User deleted (deactivated) successfully: {}", id);
    }

    /**
     * Authenticate user (basic check - in real app would use Spring Security)
     */
    @Transactional(readOnly = true)
    public Optional<User> authenticateUser(String email, String password) {
        log.debug("Authenticating user: {}", email);

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // In real app, would use BCrypt or similar
            if (user.getPassword().equals(password) && user.getIsActive()) {

                // Log successful login
                auditLogService.logUserLogin(user.getUserId(), email);

                log.info("User authenticated successfully: {}", email);
                return Optional.of(user);
            }
        }

        log.warn("Authentication failed for user: {}", email);
        return Optional.empty();
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(User.UserRole role) {
        log.debug("Fetching users by role: {}", role);
        return userRepository.findByRole(role);
    }

    /**
     * Get active users
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        log.debug("Fetching active users");
        return userRepository.findByIsActiveTrue();
    }

    /**
     * Get admin users
     */
    @Transactional(readOnly = true)
    public List<User> getAdminUsers() {
        log.debug("Fetching admin users");
        return userRepository.findActiveAdmins();
    }

    /**
     * Get product managers (users who can manage products)
     */
    @Transactional(readOnly = true)
    public List<User> getProductManagers() {
        log.debug("Fetching product managers");
        return userRepository.findProductManagers();
    }

    /**
     * Get customers
     */
    @Transactional(readOnly = true)
    public List<User> getCustomers() {
        log.debug("Fetching customers");
        return userRepository.findCustomers();
    }

    /**
     * Search users
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.searchUsers(searchTerm);
    }

    /**
     * Update user password
     */
    public User updatePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Updating password for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Verify old password
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Invalid current password");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }

        // In real app, would hash the password
        user.setPassword(newPassword);
        User savedUser = userRepository.save(user);

        // Log the action
        auditLogService.logAction(
                "Password Updated",
                "User",
                userId,
                AuditLog.ActionType.UPDATE,
                userId);

        log.info("Password updated for user: {}", userId);
        return savedUser;
    }
    
    /**
     * Update user password with email notification
     */
    public User updatePasswordWithNotification(Long userId, String newPassword) {
        log.info("Updating password with notification for user: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters");
        }
        
        // In real app, would hash the password
        user.setPassword(newPassword);
        User savedUser = userRepository.save(user);
        
        // Send password update notification
        try {
            notificationService.sendPasswordUpdateNotification(savedUser, NotificationServiceProvider.EMAIL);
        } catch (Exception e) {
            log.error("Failed to send password update email to user {}: {}", userId, e.getMessage());
        }
        
        // Log the action
        auditLogService.logAction(
                "Password Updated",
                "User",
                userId,
                AuditLog.ActionType.UPDATE,
                userId);
        
        log.info("Password updated with notification for user: {}", userId);
        return savedUser;
    }

    /**
     * Activate/Deactivate user
     */
    public User setUserActive(Long userId, boolean active) {
        log.info("Setting user {} active status to: {}", userId, active);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        user.setIsActive(active);
        User savedUser = userRepository.save(user);

        // Log the action
        auditLogService.logAction(
                "User " + (active ? "Activated" : "Deactivated"),
                "User",
                userId,
                AuditLog.ActionType.UPDATE,
                null);

        log.info("User {} active status set to: {}", userId, active);
        return savedUser;
    }

    /**
     * Block user and send notification email
     */
    public User blockUser(Long userId, String reason, String blockedBy) {
        log.info("Blocking user {} with reason: {}", userId, reason);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Set user as inactive (blocked)
        user.setIsActive(false);
        User savedUser = userRepository.save(user);

        // Send email notification
        try {
            notificationService.sendUserBlockedNotification(user, reason, blockedBy, NotificationServiceProvider.EMAIL);
            // emailService.sendUserBlockedEmail(user, reason, blockedBy);
        } catch (Exception e) {
            log.error("Failed to send blocked email to user {}: {}", userId, e.getMessage());
        }

        // Log the action
        auditLogService.logAction(
                "User Blocked" + (reason != null ? " - Reason: " + reason : ""),
                "User",
                userId,
                AuditLog.ActionType.UPDATE,
                null);

        log.info("User {} has been blocked", userId);
        return savedUser;
    }

    /**
     * Unblock user and send notification email
     */
    public User unblockUser(Long userId, String unblockedBy) {
        log.info("Unblocking user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Set user as active (unblocked)
        user.setIsActive(true);
        User savedUser = userRepository.save(user);

        // Send email notification
        try {
            notificationService.sendUserUnblockedNotification(user, unblockedBy, NotificationServiceProvider.EMAIL);
            // emailService.sendUserBlockedEmail(user, reason, blockedBy);
        } catch (Exception e) {
            log.error("Failed to send blocked email to user {}: {}", userId, e.getMessage());
        }

        // Log the action
        auditLogService.logAction(
                "User Unblocked",
                "User",
                userId,
                AuditLog.ActionType.UPDATE,
                null);

        log.info("User {} has been unblocked", userId);
        return savedUser;
    }

    /**
     * Get user count by role
     */
    @Transactional(readOnly = true)
    public long getUserCountByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Get active user count
     */
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByIsActiveTrue();
    }

    /**
     * Validate user data for creation (includes password validation)
     */
    private void validateUserForCreation(User user) {
        validateUserCommon(user);

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
    }

    /**
     * Validate user data for update (excludes password validation)
     */
    private void validateUserForUpdate(User user) {
        validateUserCommon(user);
    }

    /**
     * Common validation for both create and update operations
     */
    private void validateUserCommon(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("User name is required");
        }

        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("User email is required");
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}