package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "total_before_vat", nullable = false)
    private Integer totalBeforeVat = 0;
    
    @Column(name = "total_after_vat", nullable = false)
    private Integer totalAfterVat = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status = OrderStatus.PENDING;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private DeliveryInformation deliveryInformation;
    
    @Column(name = "vat_percentage")
    private Integer vatPercentage = 10;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Invoice invoice;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    /**
     * Add order line to this order
     */
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        recalculateTotals();
    }
    
    /**
     * Remove order line from this order
     */
    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        recalculateTotals();
    }
    
    /**
     * Recalculate order totals based on order lines
     */
    public void recalculateTotals() {
        totalBeforeVat = orderItems.stream()
                .mapToInt(OrderItem::getTotalFee)
                .sum();
        
        totalAfterVat = totalBeforeVat + (totalBeforeVat * vatPercentage / 100);
    }
    
    /**
     * Check if order can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }
    
    /**
     * Cancel the order
     */
    public void cancel() {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Order cannot be cancelled in status: " + status);
        }
        status = OrderStatus.CANCELLED;
    }
    
    /**
     * Confirm the order
     */
    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        status = OrderStatus.CONFIRMED;
    }
    
    /**
     * Get total number of items in order
     */
    public int getTotalItemCount() {
        return orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
    
    /**
     * Check if order has rush delivery items
     */
    public boolean hasRushItems() {
        return orderItems.stream()
                .anyMatch(OrderItem::isRushOrder);
    }
}