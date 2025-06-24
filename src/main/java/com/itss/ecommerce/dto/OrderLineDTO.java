package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLineDTO {
    
    private Long orderLineId;
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    @NotNull(message = "Product is required")
    private ProductDTO product;
    
    @NotNull(message = "Status is required")
    private String status;
    
    private Boolean rushOrder = false;
    
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "Total fee is required")
    @PositiveOrZero(message = "Total fee cannot be negative")
    private Integer totalFee;
    
    private String deliveryTime;
    private String instructions;
    
    // Additional computed fields
    private Integer unitPrice;
    private String formattedTotalFee;
    private String formattedUnitPrice;
    private Boolean canBeCancelled;
    private String statusDescription;
    
    /**
     * Calculate unit price from total fee
     */
    public Integer getUnitPrice() {
        if (quantity == null || quantity == 0 || totalFee == null) {
            return 0;
        }
        return totalFee / quantity;
    }
    
    /**
     * Get formatted total fee with currency
     */
    public String getFormattedTotalFee() {
        if (totalFee == null) return "N/A";
        return String.format("%,d VND", totalFee);
    }
    
    /**
     * Get formatted unit price with currency
     */
    public String getFormattedUnitPrice() {
        return String.format("%,d VND", getUnitPrice());
    }
    
    /**
     * Check if order line can be cancelled
     */
    public Boolean getCanBeCancelled() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status);
    }
    
    /**
     * Get human-readable status description
     */
    public String getStatusDescription() {
        if (status == null) return "Unknown";
        
        return switch (status) {
            case "PENDING" -> "Item is pending processing";
            case "CONFIRMED" -> "Item has been confirmed";
            case "PROCESSING" -> "Item is being processed";
            case "SHIPPED" -> "Item has been shipped";
            case "DELIVERED" -> "Item has been delivered";
            case "CANCELLED" -> "Item has been cancelled";
            default -> "Status: " + status;
        };
    }
    
    /**
     * Get delivery type description
     */
    public String getDeliveryType() {
        return Boolean.TRUE.equals(rushOrder) ? "Rush Delivery" : "Standard Delivery";
    }
    
    /**
     * Check if this is a rush order
     */
    public Boolean getIsRushOrder() {
        return Boolean.TRUE.equals(rushOrder);
    }
    
    /**
     * Get formatted quantity
     */
    public String getFormattedQuantity() {
        if (quantity == null) return "0";
        return quantity + " item" + (quantity != 1 ? "s" : "");
    }
    
    /**
     * Check if order line is completed
     */
    public Boolean getIsCompleted() {
        return "DELIVERED".equals(status);
    }
    
    /**
     * Check if order line is active
     */
    public Boolean getIsActive() {
        return !("CANCELLED".equals(status) || "DELIVERED".equals(status));
    }
    
    /**
     * Get savings if rush order (typically more expensive)
     */
    public Integer getRushOrderFee() {
        if (!Boolean.TRUE.equals(rushOrder) || product == null) {
            return 0;
        }
        // Assume rush order adds 20% to the price
        return (int) (getUnitPrice() * 0.2 * quantity);
    }
    
    /**
     * Get formatted rush order fee
     */
    public String getFormattedRushOrderFee() {
        return String.format("%,d VND", getRushOrderFee());
    }
}