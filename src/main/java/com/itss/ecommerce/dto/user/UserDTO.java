package com.itss.ecommerce.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long userId;
    
    // Password is intentionally excluded from DTO for security
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
    private String phone;
    
    @NotNull(message = "Role is required")
    private String role;
    
    private LocalDateTime registrationDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PositiveOrZero(message = "Salary cannot be negative")
    private Double salary = 0.0;
    
    private Boolean isActive = true;
    
    // Additional computed fields
    private String displayName;
    private Boolean isAdmin;
    private Boolean canManageProducts;
    private String formattedSalary;
    private String membershipDuration;
    
    /**
     * Get display name for UI
     */
    public String getDisplayName() {
        return name != null && !name.trim().isEmpty() ? name : email;
    }
    
    /**
     * Check if user is admin
     */
    public Boolean getIsAdmin() {
        return "ADMIN".equals(role);
    }
    
    /**
     * Check if user can manage products
     */
    public Boolean getCanManageProducts() {
        return "ADMIN".equals(role) || "MANAGER".equals(role);
    }
    
    /**
     * Get formatted salary with currency
     */
    public String getFormattedSalary() {
        if (salary == null) return "N/A";
        return String.format("%,.0f VND", salary);
    }
    
    /**
     * Get membership duration
     */
    public String getMembershipDuration() {
        if (registrationDate == null) return "Unknown";
        
        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(registrationDate, now).toDays();
        
        if (days < 30) {
            return days + " day" + (days != 1 ? "s" : "");
        } else if (days < 365) {
            long months = days / 30;
            return months + " month" + (months != 1 ? "s" : "");
        } else {
            long years = days / 365;
            return years + " year" + (years != 1 ? "s" : "");
        }
    }
    
    /**
     * Get role description
     */
    public String getRoleDescription() {
        if (role == null) return "Unknown";
        
        return switch (role) {
            case "ADMIN" -> "System Administrator";
            case "MANAGER" -> "Store Manager";
            case "EMPLOYEE" -> "Employee";
            case "CUSTOMER" -> "Customer";
            default -> role;
        };
    }
    
    /**
     * Check if user is customer
     */
    public Boolean getIsCustomer() {
        return "CUSTOMER".equals(role);
    }
    
    /**
     * Check if user is employee (including admin and manager)
     */
    public Boolean getIsEmployee() {
        return "ADMIN".equals(role) || "MANAGER".equals(role) || "EMPLOYEE".equals(role);
    }
    
    /**
     * Get user status
     */
    public String getUserStatus() {
        if (!Boolean.TRUE.equals(isActive)) {
            return "Inactive";
        }
        return "Active";
    }
    
    /**
     * Get formatted registration date
     */
    public String getFormattedRegistrationDate() {
        if (registrationDate == null) return "Unknown";
        return registrationDate.toLocalDate().toString();
    }
    
    /**
     * Check if user has salary (employees)
     */
    public Boolean getHasSalary() {
        return salary != null && salary > 0;
    }
    
    /**
     * Get phone number formatted for display
     */
    public String getFormattedPhone() {
        if (phone == null || phone.length() < 10) return phone;
        
        // Format Vietnamese phone numbers
        if (phone.length() == 10) {
            return String.format("%s %s %s %s", 
                phone.substring(0, 3),
                phone.substring(3, 6),
                phone.substring(6, 8),
                phone.substring(8));
        } else if (phone.length() == 11) {
            return String.format("%s %s %s %s", 
                phone.substring(0, 4),
                phone.substring(4, 7),
                phone.substring(7, 9),
                phone.substring(9));
        }
        
        return phone;
    }
}