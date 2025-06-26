package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    
    private final OrderItemListRepository orderItemListRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final DeliveryInformationRepository deliveryRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuditLogService auditLogService;
    
    /**
     * Create order from cart items
     */
    public OrderItemList createOrder(List<CartItem> cartItems, DeliveryInformation deliveryInfo) {
        log.info("Creating order with {} items", cartItems.size());
        
        // Validate cart items
        validateCartItems(cartItems);
        
        // Save delivery information
        DeliveryInformation savedDeliveryInfo = deliveryRepository.save(deliveryInfo);
        
        // Create order
        OrderItemList order = new OrderItemList();
        order.setDeliveryInfo(savedDeliveryInfo);
        order.setStatus(OrderItemList.OrderStatus.PENDING);
        
        // Create order lines
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.createOrderItem(cartItem.getProduct(), cartItem.getQuantity());
            orderItem.setOrder(order);
            order.addOrderItem(orderItem);
            
            // Reduce product stock
            Product product = cartItem.getProduct();
            product.reduceStock(cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Calculate totals
        order.recalculateTotals();
        
        // Save order
        OrderItemList savedOrder = orderItemListRepository.save(order);
        
        // Create invoice
        Invoice invoice = new Invoice();
        invoice.createInvoice(savedOrder, "Order #" + savedOrder.getOrderId());
        invoiceRepository.save(invoice);
        
        // Log the action
        auditLogService.logOrderAction(
            savedOrder.getOrderId(),
            null, // No user context in this method
            "Created",
            "Order created with " + cartItems.size() + " items"
        );
        
        log.info("Order created successfully with ID: {}", savedOrder.getOrderId());
        return savedOrder;
    }
    
    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public Optional<OrderItemList> getOrderById(Long orderId) {
        log.debug("Fetching order by ID: {}", orderId);
        return orderItemListRepository.findById(orderId);
    }
    
    /**
     * Get all orders
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getAllOrders() {
        log.debug("Fetching all orders");
        return orderItemListRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getOrdersByStatus(OrderItemList.OrderStatus status) {
        log.debug("Fetching orders by status: {}", status);
        return orderItemListRepository.findByStatus(status);
    }
    
    /**
     * Get orders by customer email
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getOrdersByCustomerEmail(String email) {
        log.debug("Fetching orders for customer: {}", email);
        return orderItemListRepository.findOrdersByCustomerEmail(email);
    }
    
    /**
     * Confirm order
     */
    public OrderItemList confirmOrder(Long orderId) {
        log.info("Confirming order: {}", orderId);
        
        OrderItemList order = orderItemListRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        order.confirm();
        OrderItemList savedOrder = orderItemListRepository.save(order);
        
        auditLogService.logOrderAction(
            orderId,
            null,
            "Confirmed",
            "Order confirmed"
        );
        
        log.info("Order {} confirmed successfully", orderId);
        return savedOrder;
    }
    
    /**
     * Cancel order
     */
    public OrderItemList cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {} with reason: {}", orderId, reason);
        
        OrderItemList order = orderItemListRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Restore product stock
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.addStock(orderItem.getQuantity());
            productRepository.save(product);
        }
        
        order.cancel();
        OrderItemList savedOrder = orderItemListRepository.save(order);
        
        auditLogService.logOrderAction(
            orderId,
            null,
            "Cancelled",
            "Order cancelled: " + reason
        );
        
        log.info("Order {} cancelled successfully", orderId);
        return savedOrder;
    }
    
    /**
     * Update order status
     */
    public OrderItemList updateOrderStatus(Long orderId, OrderItemList.OrderStatus newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);
        
        OrderItemList order = orderItemListRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        OrderItemList.OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        
        OrderItemList savedOrder = orderItemListRepository.save(order);
        
        auditLogService.logOrderAction(
            orderId,
            null,
            "Status Updated",
            String.format("Status changed from %s to %s", oldStatus, newStatus)
        );
        
        log.info("Order {} status updated from {} to {}", orderId, oldStatus, newStatus);
        return savedOrder;
    }
    
    /**
     * Get pending orders
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getPendingOrders() {
        log.debug("Fetching pending orders");
        return orderItemListRepository.findPendingOrders();
    }
    
    /**
     * Get recent orders
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getRecentOrders(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        log.debug("Fetching orders from last {} days", days);
        return orderItemListRepository.findRecentOrders(cutoff);
    }
    
    /**
     * Get order statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getOrderStatistics() {
        log.debug("Fetching order statistics");
        return orderItemListRepository.getOrderStatistics();
    }
    
    /**
     * Get total revenue for date range
     */
    @Transactional(readOnly = true)
    public Long getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating revenue between {} and {}", startDate, endDate);
        return orderItemListRepository.getTotalRevenueByDateRange(startDate, endDate);
    }
    
    /**
     * Get orders with rush delivery
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getOrdersWithRushDelivery() {
        log.debug("Fetching orders with rush delivery");
        return orderItemListRepository.findOrdersWithRushDelivery();
    }
    
    /**
     * Get cancellable orders
     */
    @Transactional(readOnly = true)
    public List<OrderItemList> getCancellableOrders() {
        log.debug("Fetching cancellable orders");
        return orderItemListRepository.findCancellableOrders();
    }
    
    /**
     * Add rush delivery to order line
     */
    public OrderItem addRushDelivery(Long orderItemId, String instructions) {
        log.info("Adding rush delivery to order line: {}", orderItemId);
        
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
            .orElseThrow(() -> new RuntimeException("Order line not found with ID: " + orderItemId));
        
        if (!orderItem.getProduct().getRushOrderSupported()) {
            throw new IllegalStateException("Product does not support rush delivery");
        }
        
        orderItem.setRushOrder(true);
        orderItem.setInstructions(instructions);
        
        OrderItem savedorderItem = orderItemRepository.save(orderItem);
        
        auditLogService.logAction(
            "Rush Delivery Added",
            "orderItem",
            orderItemId,
            AuditLog.ActionType.UPDATE,
            null
        );
        
        log.info("Rush delivery added to order line: {}", orderItemId);
        return savedorderItem;
    }
    
    /**
     * Validate cart items before creating order
     */
    private void validateCartItems(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart cannot be empty");
        }
        
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            int requestedQuantity = cartItem.getQuantity();
            
            if (product == null) {
                throw new IllegalArgumentException("Cart item contains null product");
            }
            
            if (requestedQuantity <= 0) {
                throw new IllegalArgumentException("Cart item quantity must be greater than 0");
            }
            
            if (!product.hasStock(requestedQuantity)) {
                throw new IllegalStateException(
                    String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d",
                                product.getTitle(), requestedQuantity, product.getQuantity())
                );
            }
        }
    }
    
    /**
     * Simple CartItem class for order creation
     */
    public static class CartItem {
        private Product product;
        private int quantity;
        
        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
        
        public Product getProduct() { return product; }
        public int getQuantity() { return quantity; }
    }
}