package com.itss.ecommerce.service.product.handler;

import com.itss.ecommerce.entity.Product;

/**
 * Handler interface for product type-specific operations.
 * Eliminates if-else chains in ProductService by delegating to appropriate handlers.
 */
public interface ProductTypeHandler {
    
    /**
     * Check if this handler can process the given product
     */
    boolean canHandle(Product product);
    
    /**
     * Save the product using the appropriate repository
     */
    Product save(Product product);
    
    /**
     * Update type-specific fields from updated product to existing product
     */
    void updateTypeSpecificFields(Product existing, Product updated);
}