package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Long invoiceId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED, CANCELLED
    }
    
    /**
     * Create invoice for order
     */
    public void createInvoice(Order order, String description) {
        this.order = order;
        this.description = description;
        this.paymentStatus = PaymentStatus.PENDING;
    }
    
    /**
     * Mark invoice as paid
     */
    public void markAsPaid(String transactionId, String paymentMethod) {
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }
    
    /**
     * Mark invoice as failed
     */
    public void markAsFailed() {
        this.paymentStatus = PaymentStatus.FAILED;
    }
    
    /**
     * Check if invoice is paid
     */
    public boolean isPaid() {
        return PaymentStatus.PAID.equals(paymentStatus);
    }
    
    /**
     * Check if payment is pending
     */
    public boolean isPending() {
        return PaymentStatus.PENDING.equals(paymentStatus);
    }
    
    /**
     * Get invoice total amount
     */
    public Integer getTotalAmount() {
        return order != null ? order.getTotalAfterVat() : 0;
    }
    
    /**
     * Generate invoice description
     */
    public String generateDescription() {
        if (order == null) return "No order associated";
        
        int itemCount = order.getTotalItemCount();
        return String.format("Invoice for order #%d containing %d item(s)", 
                            order.getOrderId(), itemCount);
    }
}