package com.itss.ecommerce.service;

import com.itss.ecommerce.dto.cart.CartCheckRequest;
import com.itss.ecommerce.dto.cart.CartCheckResponse;
import com.itss.ecommerce.dto.cart.CartCheckResponse.InsufficientProduct;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartService {

    @Autowired
    private final ProductRepository productRepository;

    public CartService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public CartCheckResponse checkQuantities(CartCheckRequest request) {
        List<InsufficientProduct> insufficient = new ArrayList<>();
        boolean ok = true;

        for (CartCheckRequest.CartItem item : request.getItems()) {
            Optional<Product> productOpt = productRepository.findById(item.getProductId());
            if (productOpt.isEmpty() || productOpt.get().getQuantity() < item.getQuantity()) {
                ok = false;
                Product product = productOpt.orElse(null);
                insufficient.add(new InsufficientProduct(
                        item.getProductId(),
                        product != null ? product.getTitle() : "Unknown",
                        item.getQuantity(),
                        product != null ? product.getQuantity() : 0
                ));
            }
        }

        return new CartCheckResponse(ok, insufficient);
    }
}