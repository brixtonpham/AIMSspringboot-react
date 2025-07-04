package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderItemStatus status = OrderItemStatus.PENDING;
    
    @Column(name = "rush_order_using")
    private Boolean rushOrder = false;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "total_fee", nullable = false)
    private Integer totalFee;
    
    @Column(name = "delivery_time")
    private String deliveryTime;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    public enum OrderItemStatus {
        PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    /**
     * Create order line with product and quantity
     */
    public void createOrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.totalFee = product.calculateTotalPrice(quantity);
        this.status = OrderItemStatus.PENDING;
    }
    
    /**
     * Check if this is a rush order
     */
    public boolean isRushOrder() {
        return Boolean.TRUE.equals(rushOrder);
    }
    
    /**
     * Calculate unit price from total fee
     */
    public Integer getUnitPrice() {
        if (quantity == null || quantity == 0) {
            return 0;
        }
        return totalFee / quantity;
    }
    
    /**
     * Update total fee based on current product price and quantity
     */
    public void updateTotalFee() {
        if (product != null && quantity != null) {
            this.totalFee = product.calculateTotalPrice(quantity);
        }
    }
    
    /**
     * Check if order line can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderItemStatus.PENDING || status == OrderItemStatus.CONFIRMED;
    }
    
    /**
     * Cancel this order line
     */
    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Order line cannot be cancelled in status: " + status);
        }
        status = OrderItemStatus.CANCELLED;
    }
}