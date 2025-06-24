package com.itss.ecommerce.exception;

public class PaymentProcessingException extends RuntimeException {
    
    public PaymentProcessingException(String message) {
        super(message);
    }
    
    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PaymentProcessingException forInvoice(Long invoiceId, String reason) {
        return new PaymentProcessingException(
            String.format("Payment processing failed for invoice %d: %s", invoiceId, reason)
        );
    }
    
    public static PaymentProcessingException forOrder(Long orderId, String reason) {
        return new PaymentProcessingException(
            String.format("Payment processing failed for order %d: %s", orderId, reason)
        );
    }
    
    public static PaymentProcessingException gatewayError(String gateway, String error) {
        return new PaymentProcessingException(
            String.format("Payment gateway '%s' error: %s", gateway, error)
        );
    }
}