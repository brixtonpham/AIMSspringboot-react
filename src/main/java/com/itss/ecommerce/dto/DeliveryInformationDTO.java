package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInformationDTO {
    
    private Long deliveryId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;
    
    @NotBlank(message = "Address is required")
    @Size(max = 1000, message = "Address must not exceed 1000 characters")
    private String address;
    
    @NotBlank(message = "Province is required")
    @Size(max = 100, message = "Province must not exceed 100 characters")
    private String province;
    
    @Size(max = 500, message = "Delivery message must not exceed 500 characters")
    private String deliveryMessage;
    
    @PositiveOrZero(message = "Delivery fee cannot be negative")
    private Integer deliveryFee = 0;
    
    // Additional computed fields
    private String fullAddress;
    private Boolean isComplete;
    private Boolean isValidPhone;
    private Boolean isValidEmail;
    private String formattedPhone;
    private String formattedDeliveryFee;
    
    /**
     * Get full address for display
     */
    public String getFullAddress() {
        if (address == null && province == null) return "";
        if (address == null) return province;
        if (province == null) return address;
        return address + ", " + province;
    }
    
    /**
     * Check if delivery information is complete
     */
    public Boolean getIsComplete() {
        return name != null && !name.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               province != null && !province.trim().isEmpty();
    }
    
    /**
     * Validate phone number format
     */
    public Boolean getIsValidPhone() {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return cleanPhone.matches("^[0-9]{10,11}$");
    }
    
    /**
     * Validate email format
     */
    public Boolean getIsValidEmail() {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    /**
     * Get formatted phone number
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
    
    /**
     * Get formatted delivery fee with currency
     */
    public String getFormattedDeliveryFee() {
        if (deliveryFee == null) return "Free";
        if (deliveryFee == 0) return "Free";
        return String.format("%,d VND", deliveryFee);
    }
    
    /**
     * Check if delivery is free
     */
    public Boolean getIsFreeDelivery() {
        return deliveryFee == null || deliveryFee == 0;
    }
    
    /**
     * Get delivery type based on fee
     */
    public String getDeliveryType() {
        if (getIsFreeDelivery()) {
            return "Free Delivery";
        } else if (deliveryFee <= 25000) {
            return "Standard Delivery";
        } else {
            return "Express Delivery";
        }
    }
    
    /**
     * Get estimated delivery time based on province
     */
    public String getEstimatedDeliveryTime() {
        if (province == null) return "Unknown";
        
        // Major cities get faster delivery
        String provinceLower = province.toLowerCase();
        if (provinceLower.contains("ho chi minh") || provinceLower.contains("hanoi") || 
            provinceLower.contains("hai phong") || provinceLower.contains("da nang")) {
            return "1-2 business days";
        } else {
            return "3-5 business days";
        }
    }
    
    /**
     * Check if province is a major city
     */
    public Boolean getIsMajorCity() {
        if (province == null) return false;
        String provinceLower = province.toLowerCase();
        return provinceLower.contains("ho chi minh") || provinceLower.contains("hanoi") || 
               provinceLower.contains("hai phong") || provinceLower.contains("da nang");
    }
    
    /**
     * Get short address for display in lists
     */
    public String getShortAddress() {
        if (province == null) return "No address";
        
        if (address != null && address.length() > 50) {
            return address.substring(0, 47) + "...";
        }
        
        return getFullAddress();
    }
}