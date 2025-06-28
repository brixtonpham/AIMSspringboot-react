package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.AuditLog;
import com.itss.ecommerce.entity.User;
import com.itss.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AuditLogService auditLogService;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private UserService adminService; // Using UserService as it handles admin functionality
    
    private User adminUser;
    private User customerUser;
    private User managerUser;
    private User newUser;
    
    @BeforeEach
    void setUp() {
        // Sample Admin User
        adminUser = new User();
        adminUser.setUserId(1L);
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@itss.com");
        adminUser.setPassword("admin123");
        adminUser.setRole(User.UserRole.ADMIN);
        adminUser.setIsActive(true);
        adminUser.setRegistrationDate(LocalDateTime.now());
        
        // Sample Customer User (using MANAGER role as Customer doesn't exist)
        customerUser = new User();
        customerUser.setUserId(2L);
        customerUser.setName("John Customer");
        customerUser.setEmail("john@customer.com");
        customerUser.setPassword("customer123");
        customerUser.setRole(User.UserRole.MANAGER);
        customerUser.setIsActive(true);
        customerUser.setRegistrationDate(LocalDateTime.now());
        
        // Sample Manager User
        managerUser = new User();
        managerUser.setUserId(3L);
        managerUser.setName("Jane Manager");
        managerUser.setEmail("jane@manager.com");
        managerUser.setPassword("manager123");
        managerUser.setRole(User.UserRole.MANAGER);
        managerUser.setIsActive(true);
        managerUser.setRegistrationDate(LocalDateTime.now());
        
        // Sample New User for Creation
        newUser = new User();
        newUser.setName("New User");
        newUser.setEmail("newuser@test.com");
        newUser.setPassword("newuser123");
        newUser.setRole(User.UserRole.MANAGER);
        newUser.setIsActive(true);
    }
    
    @Test
    @DisplayName("Test Admin Create User Account - UT036")
    void testAdminCreateUserAccount() {
        // Given
        User savedUser = new User();
        savedUser.setUserId(4L);
        savedUser.setName(newUser.getName());
        savedUser.setEmail(newUser.getEmail());
        savedUser.setPassword(newUser.getPassword());
        savedUser.setRole(newUser.getRole());
        savedUser.setIsActive(true);
        savedUser.setRegistrationDate(LocalDateTime.now());
        
        when(userRepository.existsByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User result = adminService.createUser(newUser);
        
        // Then
        assertThat(result)
            .isNotNull()
            .satisfies(user -> {
                assertThat(user.getUserId()).isEqualTo(4L);
                assertThat(user.getName()).isEqualTo("New User");
                assertThat(user.getEmail()).isEqualTo("newuser@test.com");
                assertThat(user.getRole()).isEqualTo(User.UserRole.MANAGER);
                assertThat(user.getIsActive()).isTrue();
            });
        
        verify(userRepository).existsByEmail(newUser.getEmail());
        verify(userRepository).save(any(User.class));
        verify(auditLogService).logAction(eq("User Created"), eq("User"), eq(4L), eq(AuditLog.ActionType.CREATE), isNull());
    }
    
    @Test
    @DisplayName("Test Admin Update User Profile - UT037")
    void testAdminUpdateUserProfile() {
        // Given
        User updatedUserData = new User();
        updatedUserData.setName("Updated Name");
        updatedUserData.setEmail("updated@email.com");
        updatedUserData.setRole(User.UserRole.MANAGER);
        
        User updatedUser = new User();
        updatedUser.setUserId(2L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@email.com");
        updatedUser.setPassword(customerUser.getPassword());
        updatedUser.setRole(User.UserRole.MANAGER);
        updatedUser.setIsActive(true);
        updatedUser.setRegistrationDate(LocalDateTime.now());
        
        when(userRepository.findById(2L)).thenReturn(Optional.of(customerUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), anyLong()))
            .thenReturn(new AuditLog());
        
        // When
        User result = adminService.updateUser(2L, updatedUserData);
        
        // Then
        assertThat(result)
            .isNotNull()
            .satisfies(user -> {
                assertThat(user.getName()).isEqualTo("Updated Name");
                assertThat(user.getEmail()).isEqualTo("updated@email.com");
                assertThat(user.getRole()).isEqualTo(User.UserRole.MANAGER);
                assertThat(user.getUserId()).isEqualTo(2L);
            });
        
        verify(userRepository).findById(2L);
        verify(userRepository).save(any(User.class));
        verify(auditLogService).logAction("User Updated", "User", 2L, AuditLog.ActionType.UPDATE, 2L);
    }
    
    @Test
    @DisplayName("Test Admin Block/Unblock User - UT038")
    void testAdminBlockUnblockUser() {
        // Given - Block User
        when(userRepository.findById(2L)).thenReturn(Optional.of(customerUser));
        when(userRepository.save(any(User.class))).thenReturn(customerUser);
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), isNull()))
            .thenReturn(new AuditLog());
        doNothing().when(emailService).sendUserBlockedEmail(any(User.class), anyString(), anyString());
        doNothing().when(emailService).sendUserUnblockedEmail(any(User.class), anyString());
        
        String blockReason = "Violation of terms";
        String blockedBy = "Admin";
        
        // When - Block User
        User blockedUser = adminService.blockUser(2L, blockReason, blockedBy);
        
        // Then - User should be blocked
        assertThat(blockedUser)
            .isNotNull()
            .satisfies(user -> assertThat(user.getIsActive()).isFalse());
        
        verify(emailService).sendUserBlockedEmail(customerUser, blockReason, blockedBy);
        verify(auditLogService).logAction(
            eq("User Blocked - Reason: " + blockReason), 
            eq("User"), 
            eq(2L), 
            eq(AuditLog.ActionType.UPDATE), 
            isNull()
        );
        
        // When - Unblock User
        String unblockedBy = "Admin";
        User unblockedUser = adminService.unblockUser(2L, unblockedBy);
        
        // Then - User should be unblocked
        assertThat(unblockedUser)
            .isNotNull()
            .satisfies(user -> assertThat(user.getIsActive()).isTrue());
        
        verify(emailService).sendUserUnblockedEmail(customerUser, unblockedBy);
        verify(auditLogService).logAction(
            eq("User Unblocked"), 
            eq("User"), 
            eq(2L), 
            eq(AuditLog.ActionType.UPDATE), 
            isNull()
        );
    }
    
    @Test
    @DisplayName("Test Admin Delete User Account - UT039")
    void testAdminDeleteUserAccount() {
        // Given
        when(userRepository.findById(2L)).thenReturn(Optional.of(customerUser));
        when(userRepository.save(any(User.class))).thenReturn(customerUser);
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), isNull()))
            .thenReturn(new AuditLog());
        
        // When
        adminService.deleteUser(2L);
        
        // Then - User should be soft deleted (set to inactive)
        verify(userRepository).findById(2L);
        verify(userRepository).save(argThat(user -> !user.getIsActive()));
        verify(auditLogService).logAction(
            eq("User Deleted"), 
            eq("User"), 
            eq(2L), 
            eq(AuditLog.ActionType.DELETE), 
            isNull()
        );
    }
    
    @Test
    @DisplayName("Test Admin View User List - UT040")
    void testAdminViewUserList() {
        // Given
        List<User> allUsers = Arrays.asList(adminUser, customerUser, managerUser);
        List<User> activeUsers = Arrays.asList(adminUser, customerUser, managerUser);
        List<User> admins = Arrays.asList(adminUser);
        List<User> customers = Arrays.asList(customerUser);
        List<User> managers = Arrays.asList(managerUser);
        
        when(userRepository.findAll()).thenReturn(allUsers);
        when(userRepository.findByIsActiveTrue()).thenReturn(activeUsers);
        when(userRepository.findActiveAdmins()).thenReturn(admins);
        when(userRepository.findCustomers()).thenReturn(customers);
        when(userRepository.findProductManagers()).thenReturn(managers);
        
        // When & Then - Get All Users
        List<User> resultAllUsers = adminService.getAllUsers();
        assertThat(resultAllUsers)
            .hasSize(3)
            .contains(adminUser, customerUser, managerUser);
        
        // When & Then - Get Active Users
        List<User> resultActiveUsers = adminService.getActiveUsers();
        assertThat(resultActiveUsers)
            .hasSize(3)
            .allMatch(User::getIsActive);
        
        // When & Then - Get Admin Users
        List<User> resultAdmins = adminService.getAdminUsers();
        assertThat(resultAdmins)
            .hasSize(1)
            .allMatch(user -> user.getRole() == User.UserRole.ADMIN);
        
        // When & Then - Get Customers (using MANAGER role)
        List<User> resultCustomers = adminService.getCustomers();
        assertThat(resultCustomers)
            .hasSize(1)
            .allMatch(user -> user.getRole() == User.UserRole.MANAGER);
        
        // When & Then - Get Product Managers
        List<User> resultManagers = adminService.getProductManagers();
        assertThat(resultManagers)
            .hasSize(1)
            .allMatch(user -> user.getRole() == User.UserRole.MANAGER);
        
        verify(userRepository).findAll();
        verify(userRepository).findByIsActiveTrue();
        verify(userRepository).findActiveAdmins();
        verify(userRepository).findCustomers();
        verify(userRepository).findProductManagers();
    }
    
    @Test
    @DisplayName("Test Admin User Role Management - UT041")
    void testAdminUserRoleManagement() {
        // Given
        when(userRepository.countByRole(User.UserRole.ADMIN)).thenReturn(1L);
        when(userRepository.countByRole(User.UserRole.MANAGER)).thenReturn(2L);
        when(userRepository.countByIsActiveTrue()).thenReturn(8L);
        
        // When - Get User Counts by Role
        long adminCount = adminService.getUserCountByRole(User.UserRole.ADMIN);
        long customerCount = adminService.getUserCountByRole(User.UserRole.MANAGER);
        long managerCount = adminService.getUserCountByRole(User.UserRole.MANAGER);
        long activeUserCount = adminService.getActiveUserCount();
        
        // Then - Verify Role Counts
        assertThat(adminCount).isEqualTo(1L);
        assertThat(customerCount).isEqualTo(2L);
        assertThat(managerCount).isEqualTo(2L);
        assertThat(activeUserCount).isEqualTo(8L);
        
        verify(userRepository).countByRole(User.UserRole.ADMIN);
        verify(userRepository, times(2)).countByRole(User.UserRole.MANAGER);
        verify(userRepository).countByIsActiveTrue();
    }
    
    @Test
    @DisplayName("Test Admin Create User with Invalid Data")
    void testAdminCreateUserWithInvalidData() {
        // Given - User with invalid email
        User invalidUser = new User();
        invalidUser.setName("Test User");
        invalidUser.setEmail("invalid-email");
        invalidUser.setPassword("123");
        invalidUser.setRole(User.UserRole.MANAGER);
        
        // When & Then - Should throw validation exception
        assertThatThrownBy(() -> adminService.createUser(invalidUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
        
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    @DisplayName("Test Admin User Not Found")
    void testAdminUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> adminService.updateUser(999L, newUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found with ID: 999");
        
        assertThatThrownBy(() -> adminService.deleteUser(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found with ID: 999");
        
        assertThatThrownBy(() -> adminService.blockUser(999L, "test", "admin"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("User not found with ID: 999");
    }
    
    @Test
    @DisplayName("Test Admin Email Already Exists")
    void testAdminEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);
        
        User duplicateEmailUser = new User();
        duplicateEmailUser.setName("Duplicate User");
        duplicateEmailUser.setEmail("existing@email.com");
        duplicateEmailUser.setPassword("password123");
        duplicateEmailUser.setRole(User.UserRole.MANAGER);
        
        // When & Then
        assertThatThrownBy(() -> adminService.createUser(duplicateEmailUser))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User with email existing@email.com already exists");
        
        verify(userRepository, never()).save(any(User.class));
    }
}
