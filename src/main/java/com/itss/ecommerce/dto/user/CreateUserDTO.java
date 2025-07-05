package com.itss.ecommerce.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    
    // Basic validation kept for data integrity
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Optional phone field - validation handled by client
    private String phone;
    
    @NotNull(message = "Role is required")
    private String role;
    
    // Basic validation for data integrity
    @PositiveOrZero(message = "Salary cannot be negative")
    private Double salary = 0.0;
    
    private Boolean isActive = true;
}