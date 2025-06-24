package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    
    private Long orderId;
    
    @NotNull(message = "Total before VAT is required")
    @PositiveOrZero(message = "Total before VAT cannot be negative")
    private Integer totalBeforeVat;
    
    @NotNull(message = "Total after VAT is required")
    @PositiveOrZero(message = "Total after VAT cannot be negative")
    private Integer totalAfterVat;
    
    @NotNull(message = "Order status is required")
    private String status;
    
    @NotNull(message = "VAT percentage is required")
    @Min(value = 0, message = "VAT percentage cannot be negative")
    @Max(value = 100, message = "VAT percentage cannot exceed 100")
    private Integer vatPercentage;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related entities
    private DeliveryInformationDTO deliveryInfo;
    private List<OrderLineDTO> orderLines;
    private InvoiceDTO invoice;
    
    // Additional computed fields
    private Integer totalItemCount;
    private Boolean hasRushItems;
    private Boolean canBeCancelled;
    private String formattedTotal;
    private String statusDescription;
    
    /**
     * Get total number of items in order
     */
    public Integer getTotalItemCount() {
        if (orderLines == null) return 0;
        return orderLines.stream()
                .mapToInt(OrderLineDTO::getQuantity)
                .sum();
    }
    
    /**
     * Check if order has rush delivery items
     */
    public Boolean getHasRushItems() {
        if (orderLines == null) return false;
        return orderLines.stream()
                .anyMatch(ol -> Boolean.TRUE.equals(ol.getRushOrder()));
    }
    
    /**
     * Check if order can be cancelled
     */
    public Boolean getCanBeCancelled() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status);
    }
    
    /**
     * Get formatted total with currency
     */
    public String getFormattedTotal() {
        if (totalAfterVat == null) return "N/A";
        return String.format("%,d VND", totalAfterVat);
    }
    
    /**
     * Get human-readable status description
     */
    public String getStatusDescription() {
        if (status == null) return "Unknown";
        
        return switch (status) {
            case "PENDING" -> "Order is pending confirmation";
            case "CONFIRMED" -> "Order has been confirmed";
            case "PROCESSING" -> "Order is being processed";
            case "SHIPPED" -> "Order has been shipped";
            case "DELIVERED" -> "Order has been delivered";
            case "CANCELLED" -> "Order has been cancelled";
            default -> "Status: " + status;
        };
    }
    
    /**
     * Get VAT amount
     */
    public Integer getVatAmount() {
        if (totalBeforeVat == null || totalAfterVat == null) return 0;
        return totalAfterVat - totalBeforeVat;
    }
    
    /**
     * Get formatted VAT amount
     */
    public String getFormattedVatAmount() {
        return String.format("%,d VND", getVatAmount());
    }
    
    /**
     * Check if order is completed
     */
    public Boolean getIsCompleted() {
        return "DELIVERED".equals(status);
    }
    
    /**
     * Check if order is active (not cancelled or delivered)
     */
    public Boolean getIsActive() {
        return !("CANCELLED".equals(status) || "DELIVERED".equals(status));
    }
}