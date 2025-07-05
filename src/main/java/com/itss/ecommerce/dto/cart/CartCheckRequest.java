package com.itss.ecommerce.dto.cart;

import lombok.Data;
import java.util.List;

@Data
public class CartCheckRequest {
    private List<CartItem> items;

    @Data
    public static class CartItem {
        private Long productId;
        private int quantity;
    }
}