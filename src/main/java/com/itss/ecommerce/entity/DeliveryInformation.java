package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "delivery_information")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInformation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long deliveryId;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "province", nullable = false, length = 100)
    private String province;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "ward", nullable = false, length = 100)
    private String ward;
    
    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "delivery_message", columnDefinition = "TEXT")
    private String deliveryMessage;
    
    @Column(name = "delivery_fee")
    private Integer deliveryFee = 0;
    
    @Column(name = "delivery_time", length = 50)
    private String deliveryTime;
    
    @Column(name = "rush_delivery_instruction", columnDefinition = "TEXT")
    private String rushDeliveryInstruction;

    /**
     * Create delivery information
     */
    public void createDeliveryInfo(String name, String phone, String email, 
                                 String address, String ward, String province, String deliveryMessage) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.ward = ward;
        this.province = province;
        this.deliveryMessage = deliveryMessage;
    }
    
    /**
     * Get full address for display
     */
    public String getFullAddress() {
        return address + ", " + ward + ", " + province;
    }
    
    /**
     * Check if delivery information is complete
     */
    public boolean isComplete() {
        return name != null && !name.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               ward != null && !ward.trim().isEmpty() &&
               province != null && !province.trim().isEmpty();
    }
    
    /**
     * Validate phone number format (basic validation)
     */
    public boolean isValidPhone() {
        if (phone == null) return false;
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return cleanPhone.matches("^[0-9]{10,11}$");
    }
    
    /**
     * Validate email format (basic validation)
     */
    public boolean isValidEmail() {
        if (email == null) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}