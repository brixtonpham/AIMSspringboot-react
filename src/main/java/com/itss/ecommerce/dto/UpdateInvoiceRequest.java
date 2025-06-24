package com.itss.ecommerce.dto;

import lombok.Data;

@Data
public class UpdateInvoiceRequest {
    
    private String description;
    
    private String paymentMethod;
}