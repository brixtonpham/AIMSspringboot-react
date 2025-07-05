package com.itss.ecommerce.controller;

import com.itss.ecommerce.dto.cart.CartCheckRequest;
import com.itss.ecommerce.dto.cart.CartCheckResponse;
import com.itss.ecommerce.service.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/check-quantities")
    public CartCheckResponse checkQuantities(@RequestBody CartCheckRequest request) {
        return cartService.checkQuantities(request);
    }
}