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
    
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final ProductRepository productRepository;
    private final DeliveryInformationRepository deliveryRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuditLogService auditLogService;
    
    /**
     * Create order from cart items
     */
    public Order createOrder(List<CartItem> cartItems, DeliveryInformation deliveryInfo) {
        log.info("Creating order with {} items", cartItems.size());
        
        // Validate cart items
        validateCartItems(cartItems);
        
        // Save delivery information
        DeliveryInformation savedDeliveryInfo = deliveryRepository.save(deliveryInfo);
        
        // Create order
        Order order = new Order();
        order.setDeliveryInfo(savedDeliveryInfo);
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Create order lines
        for (CartItem cartItem : cartItems) {
            OrderLine orderLine = new OrderLine();
            orderLine.createOrderLine(cartItem.getProduct(), cartItem.getQuantity());
            orderLine.setOrder(order);
            order.addOrderLine(orderLine);
            
            // Reduce product stock
            Product product = cartItem.getProduct();
            product.reduceStock(cartItem.getQuantity());
            productRepository.save(product);
        }
        
        // Calculate totals
        order.recalculateTotals();
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
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
    public Optional<Order> getOrderById(Long orderId) {
        log.debug("Fetching order by ID: {}", orderId);
        return orderRepository.findById(orderId);
    }
    
    /**
     * Get all orders
     */
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        log.debug("Fetching all orders");
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        log.debug("Fetching orders by status: {}", status);
        return orderRepository.findByStatus(status);
    }
    
    /**
     * Get orders by customer email
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerEmail(String email) {
        log.debug("Fetching orders for customer: {}", email);
        return orderRepository.findOrdersByCustomerEmail(email);
    }
    
    /**
     * Confirm order
     */
    public Order confirmOrder(Long orderId) {
        log.info("Confirming order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        order.confirm();
        Order savedOrder = orderRepository.save(order);
        
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
    public Order cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {} with reason: {}", orderId, reason);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Restore product stock
        for (OrderLine orderLine : order.getOrderLines()) {
            Product product = orderLine.getProduct();
            product.addStock(orderLine.getQuantity());
            productRepository.save(product);
        }
        
        order.cancel();
        Order savedOrder = orderRepository.save(order);
        
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
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        
        Order savedOrder = orderRepository.save(order);
        
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
    public List<Order> getPendingOrders() {
        log.debug("Fetching pending orders");
        return orderRepository.findPendingOrders();
    }
    
    /**
     * Get recent orders
     */
    @Transactional(readOnly = true)
    public List<Order> getRecentOrders(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        log.debug("Fetching orders from last {} days", days);
        return orderRepository.findRecentOrders(cutoff);
    }
    
    /**
     * Get order statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getOrderStatistics() {
        log.debug("Fetching order statistics");
        return orderRepository.getOrderStatistics();
    }
    
    /**
     * Get total revenue for date range
     */
    @Transactional(readOnly = true)
    public Long getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating revenue between {} and {}", startDate, endDate);
        return orderRepository.getTotalRevenueByDateRange(startDate, endDate);
    }
    
    /**
     * Get orders with rush delivery
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersWithRushDelivery() {
        log.debug("Fetching orders with rush delivery");
        return orderRepository.findOrdersWithRushDelivery();
    }
    
    /**
     * Get cancellable orders
     */
    @Transactional(readOnly = true)
    public List<Order> getCancellableOrders() {
        log.debug("Fetching cancellable orders");
        return orderRepository.findCancellableOrders();
    }
    
    /**
     * Add rush delivery to order line
     */
    public OrderLine addRushDelivery(Long orderLineId, String instructions) {
        log.info("Adding rush delivery to order line: {}", orderLineId);
        
        OrderLine orderLine = orderLineRepository.findById(orderLineId)
            .orElseThrow(() -> new RuntimeException("Order line not found with ID: " + orderLineId));
        
        if (!orderLine.getProduct().getRushOrderSupported()) {
            throw new IllegalStateException("Product does not support rush delivery");
        }
        
        orderLine.setRushOrder(true);
        orderLine.setInstructions(instructions);
        
        OrderLine savedOrderLine = orderLineRepository.save(orderLine);
        
        auditLogService.logAction(
            "Rush Delivery Added",
            "OrderLine",
            orderLineId,
            AuditLog.ActionType.UPDATE,
            null
        );
        
        log.info("Rush delivery added to order line: {}", orderLineId);
        return savedOrderLine;
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