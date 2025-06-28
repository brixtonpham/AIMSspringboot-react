package com.itss.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentTransaction> paymentTransactions = new ArrayList<>();
    
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
     * Add payment transaction
     */
    public void addPaymentTransaction(PaymentTransaction transaction) {
        paymentTransactions.add(transaction);
        transaction.setInvoice(this);
    }
    
    /**
     * Mark invoice as paid based on successful transaction
     */
    public void markAsPaid(PaymentTransaction successfulTransaction) {
        this.paymentMethod = successfulTransaction.getPaymentMethod();
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
     * Get successful payment transaction
     */
    public PaymentTransaction getSuccessfulTransaction() {
        return paymentTransactions.stream()
                .filter(PaymentTransaction::isSuccessful)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Get latest payment transaction
     */
    public PaymentTransaction getLatestTransaction() {
        return paymentTransactions.stream()
                .max((t1, t2) -> t1.getCreatedAt().compareTo(t2.getCreatedAt()))
                .orElse(null);
    }
    
    /**
     * Check if there are any pending transactions
     */
    public boolean hasPendingTransactions() {
        return paymentTransactions.stream()
                .anyMatch(PaymentTransaction::isPending);
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