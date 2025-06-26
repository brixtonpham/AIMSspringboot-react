package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    
    private Long productId;
    
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Integer price;
    
    @NotNull(message = "Product value is required")
    @Positive(message = "Product value must be positive")
    private Integer productValue; // Used for price validation (30%-150% of this value)
    
    // Physical product attributes
    private String dimensions; // e.g., "20x15x3 cm"
    private String condition; // New, Used - Like New, Used - Very Good, etc.
    
    @PositiveOrZero(message = "Weight cannot be negative")
    private Float weight;
    
    private Boolean rushOrderSupported = false;
    
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;
    
    @Size(max = 50, message = "Barcode must not exceed 50 characters")
    private String barcode;
    
    private String importDate;
    
    @Size(max = 1000, message = "Introduction must not exceed 1000 characters")
    private String introduction;
    
    @PositiveOrZero(message = "Quantity cannot be negative")
    private Integer quantity = 0;
    
    @NotBlank(message = "Product type is required")
    private String type;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional computed fields for API
    private Boolean inStock;
    private String formattedPrice;
    private String availabilityStatus;
    
    /**
     * Check if product is in stock
     */
    public Boolean getInStock() {
        return quantity != null && quantity > 0;
    }
    
    /**
     * Get formatted price with currency
     */
    public String getFormattedPrice() {
        if (price == null) return "N/A";
        return String.format("%,d VND", price);
    }
    
    /**
     * Get availability status text
     */
    public String getAvailabilityStatus() {
        if (quantity == null || quantity <= 0) {
            return "Out of Stock";
        } else if (quantity < 10) {
            return "Low Stock";
        } else {
            return "In Stock";
        }
    }
}