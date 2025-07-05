package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.itss.ecommerce.dto.product.BookDTO;
import com.itss.ecommerce.dto.product.CDDTO;
import com.itss.ecommerce.dto.product.DVDDTO;
import com.itss.ecommerce.dto.product.LPDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFormRequest {
    
    @Valid
    @NotNull(message = "Product data is required")
    private ProductDTO productData;
    
    // Type-specific data - only one should be populated based on product type
    // No @Valid annotation to avoid validation errors when null or empty
    private BookDTO bookData;
    
    private CDDTO cdData;
    
    private DVDDTO dvdData;
    
    private LPDTO lpData;
    
    /**
     * Get the product type from the base product data
     */
    public String getProductType() {
        return productData != null ? productData.getType() : null;
    }
    
    /**
     * Check if this is a book request
     */
    public boolean isBook() {
        return "book".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Check if this is a CD request
     */
    public boolean isCD() {
        return "cd".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Check if this is a DVD request
     */
    public boolean isDVD() {
        return "dvd".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Check if this is an LP request
     */
    public boolean isLP() {
        return "lp".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Get the appropriate type-specific DTO based on product type
     */
    public Object getTypeSpecificData() {
        if (isBook()) return bookData;
        if (isCD()) return cdData;
        if (isDVD()) return dvdData;
        if (isLP()) return lpData;
        return null;
    }
}