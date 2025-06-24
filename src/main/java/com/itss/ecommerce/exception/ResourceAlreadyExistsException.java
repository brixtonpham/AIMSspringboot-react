package com.itss.ecommerce.exception;

public class ResourceAlreadyExistsException extends RuntimeException {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ResourceAlreadyExistsException forEntity(String entityType, String identifier) {
        return new ResourceAlreadyExistsException(
            String.format("%s with identifier '%s' already exists", entityType, identifier)
        );
    }
    
    public static ResourceAlreadyExistsException forProduct(String barcode) {
        return new ResourceAlreadyExistsException(
            String.format("Product with barcode '%s' already exists", barcode)
        );
    }
    
    public static ResourceAlreadyExistsException forUser(String email) {
        return new ResourceAlreadyExistsException(
            String.format("User with email '%s' already exists", email)
        );
    }
}