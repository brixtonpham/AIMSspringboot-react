package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "password", nullable = false)
    private String password; // Should be encrypted in real app
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.CUSTOMER;
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    
    @Column(name = "salary")
    private Double salary = 0.0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }
    
    public enum UserRole {
        ADMIN, CUSTOMER, MANAGER, EMPLOYEE
    }
    
    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }
    
    /**
     * Check if user can manage products
     */
    public boolean canManageProducts() {
        return role == UserRole.ADMIN || role == UserRole.MANAGER;
    }
    
    /**
     * Get display name for UI
     */
    public String getDisplayName() {
        return name != null ? name : email;
    }
}