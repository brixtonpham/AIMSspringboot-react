package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Book.class, name = "book"),
    @JsonSubTypes.Type(value = CD.class, name = "cd"),
    @JsonSubTypes.Type(value = DVD.class, name = "dvd")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    
    @Column(name = "title", nullable = false, length = 255)
    private String title;
    
    @Column(name = "price", nullable = false)
    private Integer price;
    
    @Column(name = "weight")
    private Float weight;
    
    @Column(name = "rush_order_supported")
    private Boolean rushOrderSupported = false;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "barcode", unique = true, length = 50)
    private String barcode;
    
    @Column(name = "import_date")
    private String importDate;
    
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;
    
    @Column(name = "quantity")
    private Integer quantity = 0;
    
    @Column(name = "type", insertable = false, updatable = false)
    private String type;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Abstract method to get the product type
     * Must be implemented by subclasses
     */
    public abstract String getProductType();
    
    /**
     * Check if product has sufficient quantity for order
     */
    public boolean hasStock(int requestedQuantity) {
        return quantity != null && quantity >= requestedQuantity;
    }
    
    /**
     * Reduce stock quantity
     */
    public void reduceStock(int amount) {
        if (quantity != null && quantity >= amount) {
            quantity -= amount;
        } else {
            throw new IllegalStateException("Insufficient stock for product: " + title);
        }
    }
    
    /**
     * Add stock quantity
     */
    public void addStock(int amount) {
        quantity = (quantity == null ? 0 : quantity) + amount;
    }
    
    /**
     * Calculate total price for given quantity
     */
    public Integer calculateTotalPrice(int requestedQuantity) {
        return price * requestedQuantity;
    }
}