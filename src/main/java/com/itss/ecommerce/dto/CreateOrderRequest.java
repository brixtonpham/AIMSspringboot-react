package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotNull(message = "Cart items are required")
    @NotEmpty(message = "Cart cannot be empty")
    @Valid
    private List<CartItemDTO> cartItems;
    
    @NotNull(message = "Delivery information is required")
    @Valid
    private DeliveryInformationDTO deliveryInfo;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        
        @NotNull(message = "Product ID is required")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        private Boolean rushOrder = false;
        private String instructions;
        
        // Additional validation fields
        private String productTitle; // For display purposes
        private Integer unitPrice;   // For display purposes
        
        /**
         * Get total price for this item
         */
        public Integer getTotalPrice() {
            if (unitPrice == null || quantity == null) return 0;
            return unitPrice * quantity;
        }
        
        /**
         * Get formatted total price
         */
        public String getFormattedTotalPrice() {
            return String.format("%,d VND", getTotalPrice());
        }
        
        /**
         * Check if this is a rush order item
         */
        public Boolean getIsRushOrder() {
            return Boolean.TRUE.equals(rushOrder);
        }
    }
    
    /**
     * Get total number of items in cart
     */
    public Integer getTotalItems() {
        if (cartItems == null) return 0;
        return cartItems.stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }
    
    /**
     * Get total cart value (if unit prices are provided)
     */
    public Integer getTotalValue() {
        if (cartItems == null) return 0;
        return cartItems.stream()
                .mapToInt(CartItemDTO::getTotalPrice)
                .sum();
    }
    
    /**
     * Check if cart has rush order items
     */
    public Boolean getHasRushItems() {
        if (cartItems == null) return false;
        return cartItems.stream()
                .anyMatch(CartItemDTO::getIsRushOrder);
    }
    
    /**
     * Get formatted total value
     */
    public String getFormattedTotalValue() {
        return String.format("%,d VND", getTotalValue());
    }
    
    /**
     * Validate cart items
     */
    public void validateCartItems() {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }
        
        for (CartItemDTO item : cartItems) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID is required for all cart items");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for all cart items");
            }
        }
    }
    
    /**
     * Get unique product count
     */
    public Integer getUniqueProductCount() {
        if (cartItems == null) return 0;
        return (int) cartItems.stream()
                .map(CartItemDTO::getProductId)
                .distinct()
                .count();
    }
    
    /**
     * Get cart summary for display
     */
    public String getCartSummary() {
        int totalItems = getTotalItems();
        int uniqueProducts = getUniqueProductCount();
        
        return String.format("%d item%s (%d unique product%s)",
                totalItems, totalItems != 1 ? "s" : "",
                uniqueProducts, uniqueProducts != 1 ? "s" : "");
    }
}