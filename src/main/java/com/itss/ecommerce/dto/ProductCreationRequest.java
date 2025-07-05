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
public class ProductCreationRequest {
    
    @Valid
    @NotNull(message = "Product data is required")
    private ProductDTO productData;
    
    // Type-specific data - only one should be populated based on product type
    @Valid
    private BookDTO bookData;
    
    @Valid
    private CDDTO cdData;
    
    @Valid
    private DVDDTO dvdData;
    
    @Valid
    private LPDTO lpData;
    
    /**
     * Get the product type from the base product data
     */
    public String getProductType() {
        return productData != null ? productData.getType() : null;
    }
    
    /**
     * Check if this is a book creation request
     */
    public boolean isBook() {
        return "book".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Check if this is a CD creation request
     */
    public boolean isCD() {
        return "cd".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Check if this is a DVD creation request
     */
    public boolean isDVD() {
        return "dvd".equalsIgnoreCase(getProductType());
    }
    
    /**
     * Check if this is an LP creation request
     */
    public boolean isLP() {
        return "lp".equalsIgnoreCase(getProductType());
    }
}