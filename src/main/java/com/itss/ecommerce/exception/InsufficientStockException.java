package com.itss.ecommerce.exception;

public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static InsufficientStockException forProduct(String productTitle, int requested, int available) {
        return new InsufficientStockException(
            String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                         productTitle, requested, available)
        );
    }
    
    public static InsufficientStockException forProductId(Long productId, int requested, int available) {
        return new InsufficientStockException(
            String.format("Insufficient stock for product ID %d. Requested: %d, Available: %d",
                         productId, requested, available)
        );
    }
}