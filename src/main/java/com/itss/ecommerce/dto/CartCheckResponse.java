package com.itss.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CartCheckResponse {
    private boolean ok;
    private List<InsufficientProduct> insufficient;

    @Data
    @AllArgsConstructor
    public static class InsufficientProduct {
        private Long productId;
        private String title;
        private int requested;
        private int available;
    }
}