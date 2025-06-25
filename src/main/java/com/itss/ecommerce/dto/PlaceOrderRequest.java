package com.itss.ecommerce.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
public class PlaceOrderRequest {
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;
    
    @NotNull(message = "Delivery information is required")
    private DeliveryInformationRequest deliveryInfo;
    
    private String notes;
    
    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
    }
    
    @Data
    public static class DeliveryInformationRequest {
        @NotNull(message = "Name is required")
        private String name;
        
        @NotNull(message = "Phone is required")
        private String phone;
        
        @NotNull(message = "Address is required")
        private String address;
        
        private String province;
        private String district;
        private String ward;
    }
}