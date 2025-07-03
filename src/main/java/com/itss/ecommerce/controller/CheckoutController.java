package com.itss.ecommerce.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itss.ecommerce.dto.ApiResponse;
import com.itss.ecommerce.dto.CreateOrderRequest;
import com.itss.ecommerce.dto.DeliveryInformationDTO;
import com.itss.ecommerce.dto.OrderDTO;
import com.itss.ecommerce.dto.mapper.DeliveryInformationMapper;
import com.itss.ecommerce.dto.mapper.OrderMapper;
import com.itss.ecommerce.entity.DeliveryInformation;
import com.itss.ecommerce.entity.Order;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.service.OrderService;
import com.itss.ecommerce.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * CheckoutController.java
 * Handles order creation and checkout process
 * This controller provides an endpoint to create a new order based on the items in the user's cart.
 * It validates the request, converts cart items to the appropriate format, and calls the OrderService
 * to process the order. The response includes the created order details.
 * This controller process the request and convert reqest body from client's datum to Data T
 */
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class CheckoutController {
    private final OrderService orderService;
    private final ProductService productService;    
    /**
     * Create new order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating new order with {} items",
                request.getCartItems().size());

        // Convert cart items
        List<OrderService.CartItem> cartItems = request.getCartItems().stream()
                .map(this::convertToCartItem)
                .collect(Collectors.toList());

        // Convert delivery info
        DeliveryInformation deliveryInfo = DeliveryInformationMapper.toEntity(request.getDeliveryInfo());

        Order savedOrder = orderService.createOrder(cartItems, deliveryInfo);
        System.out.println("Saved Order: " + savedOrder.getOrderId());
        OrderDTO orderDTO = OrderMapper.toDTO(savedOrder);

        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderDTO, "Order created successfully"));
    }
    /**
     * Convert CreateOrderRequest.CartItemDTO to OrderService.CartItem
     */
    private OrderService.CartItem convertToCartItem(CreateOrderRequest.CartItemDTO cartItemDTO) {
        Optional<Product> product = productService.getProductById(cartItemDTO.getProductId());
        if (product.isEmpty()) {
            throw new IllegalArgumentException("Product not found with ID: " + cartItemDTO.getProductId());
        }

        return new OrderService.CartItem(product.get(), cartItemDTO.getQuantity());
    }
}
