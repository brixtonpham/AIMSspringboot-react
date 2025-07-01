package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {
    
    private Long invoiceId;
    
    @NotNull(message = "Order ID is required")
    private Long orderId;
    
    private Long transactionId;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private LocalDateTime createdAt;
    
    @NotNull(message = "Payment status is required")
    private String paymentStatus;
    
    @Size(max = 50, message = "Payment method must not exceed 50 characters")
    private String paymentMethod;
    
    private LocalDateTime paidAt;
    
    // Additional computed fields
    private Integer totalAmount;
    private String formattedTotalAmount;
    private Boolean isPaid;
    private Boolean isPending;
    private String statusDescription;
    private String formattedCreatedAt;
    private String formattedPaidAt;
    private String paymentDuration;
    
    /**
     * Get total amount from associated order
     */
    public Integer getTotalAmount() {
        // This would typically be populated from the order
        return totalAmount;
    }
    
    /**
     * Get formatted total amount with currency
     */
    public String getFormattedTotalAmount() {
        if (totalAmount == null) return "N/A";
        return String.format("%,d VND", totalAmount);
    }
    
    /**
     * Check if invoice is paid
     */
    public Boolean getIsPaid() {
        return "PAID".equals(paymentStatus);
    }
    
    /**
     * Check if payment is pending
     */
    public Boolean getIsPending() {
        return "PENDING".equals(paymentStatus);
    }
    
    /**
     * Get human-readable status description
     */
    public String getStatusDescription() {
        if (paymentStatus == null) return "Unknown";
        
        return switch (paymentStatus) {
            case "PENDING" -> "Payment is pending";
            case "PAID" -> "Payment completed successfully";
            case "FAILED" -> "Payment failed";
            case "REFUNDED" -> "Payment has been refunded";
            case "CANCELLED" -> "Payment was cancelled";
            default -> "Status: " + paymentStatus;
        };
    }
    
    /**
     * Get formatted creation date
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "Unknown";
        return createdAt.toLocalDate().toString() + " " + 
               createdAt.toLocalTime().toString().substring(0, 8);
    }
    
    /**
     * Get formatted payment date
     */
    public String getFormattedPaidAt() {
        if (paidAt == null) return "Not paid";
        return paidAt.toLocalDate().toString() + " " + 
               paidAt.toLocalTime().toString().substring(0, 8);
    }
    
    /**
     * Get payment duration (time between creation and payment)
     */
    public String getPaymentDuration() {
        if (createdAt == null || paidAt == null) return "N/A";
        
        long minutes = java.time.Duration.between(createdAt, paidAt).toMinutes();
        
        if (minutes < 60) {
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else if (minutes < 1440) { // 24 hours
            long hours = minutes / 60;
            return hours + " hour" + (hours != 1 ? "s" : "");
        } else {
            long days = minutes / 1440;
            return days + " day" + (days != 1 ? "s" : "");
        }
    }
    
    /**
     * Check if invoice is overdue (pending for more than specified days)
     */
    public Boolean getIsOverdue() {
        return getIsOverdue(7); // Default to 7 days
    }
    
    /**
     * Check if invoice is overdue for specified days
     */
    public Boolean getIsOverdue(int days) {
        if (!getIsPending() || createdAt == null) return false;
        
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return createdAt.isBefore(cutoff);
    }
    
    /**
     * Get days since creation
     */
    public Long getDaysSinceCreation() {
        if (createdAt == null) return 0L;
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }
    
    /**
     * Get payment method description
     */
    public String getPaymentMethodDescription() {
        if (paymentMethod == null) return "Not specified";
        
        return switch (paymentMethod.toUpperCase()) {
            case "VNPAY" -> "VNPay E-Wallet";
            case "CREDIT_CARD" -> "Credit Card";
            case "DEBIT_CARD" -> "Debit Card";
            case "BANK_TRANSFER" -> "Bank Transfer";
            case "CASH" -> "Cash on Delivery";
            case "MOMO" -> "MoMo E-Wallet";
            case "ZALOPAY" -> "ZaloPay E-Wallet";
            default -> paymentMethod;
        };
    }
    
    /**
     * Check if payment method is digital
     */
    public Boolean getIsDigitalPayment() {
        if (paymentMethod == null) return false;
        String method = paymentMethod.toLowerCase();
        return method.contains("vnpay") || method.contains("momo") || 
               method.contains("zalo") || method.contains("card") ||
               method.contains("bank");
    }
    
    /**
     * Get invoice reference number for display
     */
    public String getInvoiceReference() {
        if (invoiceId == null) return "N/A";
        return String.format("INV-%06d", invoiceId);
    }
    
    /**
     * Check if invoice can be refunded
     */
    public Boolean getCanBeRefunded() {
        return "PAID".equals(paymentStatus);
    }
}