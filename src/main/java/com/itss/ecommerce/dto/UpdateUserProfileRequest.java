package com.itss.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String phone;
    
    private String address;
}