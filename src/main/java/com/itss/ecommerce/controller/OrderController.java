package com.itss.ecommerce.controller;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.dto.mapper.OrderMapper;
import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.service.OrderService;
import com.itss.ecommerce.service.ProductService;
import com.itss.ecommerce.service.OrderService.CartItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final OrderMapper orderMapper;

    /**
     * Create new order
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemListDTO>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        log.info("POST /api/orders - Creating new order with {} items",
                request.getCartItems().size());

        // Convert cart items
        List<OrderService.CartItem> cartItems = request.getCartItems().stream()
                .map(this::convertToCartItem)
                .collect(Collectors.toList());

        // Convert delivery info
        DeliveryInformation deliveryInfo = orderMapper.toEntity(request.getDeliveryInfo());
        System.out.println("Delivery Info: " + request.getDeliveryInfo());

        System.out.println("Delivery Info: " + deliveryInfo);

        OrderItemList savedOrder = orderService.createOrder(cartItems, deliveryInfo);
        System.out.println("Saved Order: " + savedOrder.getOrderId());
        OrderItemListDTO orderDTO = orderMapper.toDTO(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(orderDTO, "Order created successfully"));
    }

    /**
     * Get all orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getAllOrders() {
        log.info("GET /api/orders - Fetching all orders");

        List<OrderItemList> orders = orderService.getAllOrders();
        List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

        return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                String.format("Retrieved %d orders", orderDTOs.size())));
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemListDTO>> getOrderById(
            @PathVariable @Positive Long id) {
        log.info("GET /api/orders/{} - Fetching order", id);

        Optional<OrderItemList> order = orderService.getOrderById(id);
        if (order.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("Order not found with ID: " + id));
        }

        OrderItemListDTO orderDTO = orderMapper.toDTO(order.get());
        return ResponseEntity.ok(ApiResponse.success(orderDTO));
    }

    /**
     * Get orders by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getOrdersByStatus(
            @PathVariable String status) {
        log.info("GET /api/orders/status/{} - Fetching orders", status);

        try {
            OrderItemList.OrderStatus orderStatus = OrderItemList.OrderStatus.valueOf(status.toUpperCase());
            List<OrderItemList> orders = orderService.getOrdersByStatus(orderStatus);
            List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

            return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                    String.format("Retrieved %d orders with status %s", orderDTOs.size(), status)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Invalid order status: " + status));
        }
    }

    /**
     * Get orders by customer email
     */
    @GetMapping("/customer/{email}")
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getOrdersByCustomerEmail(
            @PathVariable String email) {
        log.info("GET /api/orders/customer/{} - Fetching orders", email);

        List<OrderItemList> orders = orderService.getOrdersByCustomerEmail(email);
        List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

        return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                String.format("Retrieved %d orders for customer %s", orderDTOs.size(), email)));
    }

    /**
     * Confirm order
     */
    @PatchMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<OrderItemListDTO>> confirmOrder(
            @PathVariable @Positive Long id) {
        log.info("PATCH /api/orders/{}/confirm - Confirming order", id);

        OrderItemList confirmedOrder = orderService.confirmOrder(id);
        OrderItemListDTO orderDTO = orderMapper.toDTO(confirmedOrder);

        return ResponseEntity.ok(ApiResponse.success(orderDTO, "Order confirmed successfully"));
    }

    /**
     * Cancel order
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderItemListDTO>> cancelOrder(
            @PathVariable @Positive Long id,
            @RequestParam(required = false, defaultValue = "Customer request") String reason) {
        log.info("PATCH /api/orders/{}/cancel - Cancelling order with reason: {}", id, reason);

        OrderItemList cancelledOrder = orderService.cancelOrder(id, reason);
        OrderItemListDTO orderDTO = orderMapper.toDTO(cancelledOrder);

        return ResponseEntity.ok(ApiResponse.success(orderDTO, "Order cancelled successfully"));
    }

    /**
     * Update order status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderItemListDTO>> updateOrderStatus(
            @PathVariable @Positive Long id,
            @RequestParam String status) {
        log.info("PATCH /api/orders/{}/status - Updating status to {}", id, status);

        try {
            OrderItemList.OrderStatus orderStatus = OrderItemList.OrderStatus.valueOf(status.toUpperCase());
            OrderItemList updatedOrder = orderService.updateOrderStatus(id, orderStatus);
            OrderItemListDTO orderDTO = orderMapper.toDTO(updatedOrder);

            return ResponseEntity.ok(ApiResponse.success(orderDTO,
                    "Order status updated to " + status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Invalid order status: " + status));
        }
    }

    /**
     * Get pending orders
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getPendingOrders() {
        log.info("GET /api/orders/pending - Fetching pending orders");

        List<OrderItemList> orders = orderService.getPendingOrders();
        List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

        return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                String.format("Retrieved %d pending orders", orderDTOs.size())));
    }

    /**
     * Get recent orders
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getRecentOrders(
            @RequestParam(defaultValue = "30") @Positive int days) {
        log.info("GET /api/orders/recent - Fetching orders from last {} days", days);

        List<OrderItemList> orders = orderService.getRecentOrders(days);
        List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

        return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                String.format("Retrieved %d orders from last %d days", orderDTOs.size(), days)));
    }

    /**
     * Get order statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<List<Object[]>>> getOrderStatistics() {
        log.info("GET /api/orders/statistics - Fetching order statistics");

        List<Object[]> statistics = orderService.getOrderStatistics();

        return ResponseEntity.ok(ApiResponse.success(statistics, "Order statistics retrieved"));
    }

    /**
     * Get total revenue for date range
     */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<Long>> getTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/orders/revenue - Calculating revenue between {} and {}", startDate, endDate);

        Long revenue = orderService.getTotalRevenue(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(revenue,
                String.format("Total revenue: %,d VND", revenue != null ? revenue : 0)));
    }

    /**
     * Get orders with rush delivery
     */
    @GetMapping("/rush-delivery")
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getOrdersWithRushDelivery() {
        log.info("GET /api/orders/rush-delivery - Fetching orders with rush delivery");

        List<OrderItemList> orders = orderService.getOrdersWithRushDelivery();
        List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

        return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                String.format("Retrieved %d orders with rush delivery", orderDTOs.size())));
    }

    /**
     * Get cancellable orders
     */
    @GetMapping("/cancellable")
    public ResponseEntity<ApiResponse<List<OrderItemListDTO>>> getCancellableOrders() {
        log.info("GET /api/orders/cancellable - Fetching cancellable orders");

        List<OrderItemList> orders = orderService.getCancellableOrders();
        List<OrderItemListDTO> orderDTOs = orderMapper.toDTOList(orders);

        return ResponseEntity.ok(ApiResponse.success(orderDTOs,
                String.format("Retrieved %d cancellable orders", orderDTOs.size())));
    }

    /**
     * Add rush delivery to order line
     */
    @PatchMapping("/lines/{orderLineId}/rush-delivery")
    public ResponseEntity<ApiResponse<OrderItemDTO>> addRushDelivery(
            @PathVariable @Positive Long orderLineId,
            @RequestParam(required = false) String instructions) {
        log.info("PATCH /api/orders/lines/{}/rush-delivery - Adding rush delivery", orderLineId);

        OrderItem updatedOrderLine = orderService.addRushDelivery(orderLineId, instructions);
        OrderItemDTO orderLineDTO = orderMapper.toDTO(updatedOrderLine);

        return ResponseEntity.ok(ApiResponse.success(orderLineDTO,
                "Rush delivery added successfully"));
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