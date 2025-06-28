package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private DeliveryInformationRepository deliveryRepository;
    
    @Mock
    private InvoiceRepository invoiceRepository;
    
    @Mock
    private AuditLogService auditLogService;
    
    @InjectMocks
    private OrderService orderService;
    
    private Order sampleOrder;
    private List<OrderService.CartItem> cartItems;
    private DeliveryInformation deliveryInfo;
    private Product sampleProduct;
    
    @BeforeEach
    void setUp() {
        // Sample Product
        sampleProduct = new Book();
        sampleProduct.setProductId(1L);
        sampleProduct.setTitle("The Great Gatsby");
        sampleProduct.setPrice(150000);
        sampleProduct.setQuantity(10);
        sampleProduct.setWeight(0.5f);
        sampleProduct.setRushOrderSupported(true);
        
        // Sample Delivery Information
        deliveryInfo = new DeliveryInformation();
        deliveryInfo.setDeliveryId(1L);
        deliveryInfo.setName("John Doe");
        deliveryInfo.setPhone("0123456789");
        deliveryInfo.setEmail("john.doe@example.com");
        deliveryInfo.setProvince("Ho Chi Minh City");
        deliveryInfo.setDistrict("District 1");
        deliveryInfo.setWard("Ward 1");
        deliveryInfo.setAddress("123 Main Street");
        deliveryInfo.setDeliveryMessage("Please call before delivery");
        deliveryInfo.setDeliveryFee(25000);
        
        // Sample Cart Items
        cartItems = new ArrayList<>();
        cartItems.add(new OrderService.CartItem(sampleProduct, 2));
        
        // Sample Order
        sampleOrder = new Order();
        sampleOrder.setOrderId(1L);
        sampleOrder.setStatus(Order.OrderStatus.PENDING);
        sampleOrder.setDeliveryInformation(deliveryInfo);
        sampleOrder.setTotalBeforeVat(300000);
        sampleOrder.setTotalAfterVat(330000);
        sampleOrder.setVatPercentage(10);
        sampleOrder.setCreatedAt(LocalDateTime.now());
        sampleOrder.setUpdatedAt(LocalDateTime.now());
        
        // Add order items
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(1L);
        orderItem.setProduct(sampleProduct);
        orderItem.setQuantity(2);
        orderItem.setTotalFee(300000);
        orderItem.setStatus(OrderItem.OrderItemStatus.PENDING);
        orderItem.setOrder(sampleOrder);
        
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        sampleOrder.setOrderItems(orderItems);
    }
    
    @Test
    @DisplayName("Test Process Pending Order Status Update - UT026")
    void testProcessPendingOrderStatusUpdate() {
        // Given - Arrange test data
        Long orderId = 1L;
        Order.OrderStatus newStatus = Order.OrderStatus.PROCESSING;
        Order.OrderStatus oldStatus = Order.OrderStatus.PENDING;
        
        Order updatedOrder = new Order();
        updatedOrder.setOrderId(orderId);
        updatedOrder.setStatus(newStatus);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);
        when(auditLogService.logOrderAction(anyLong(), any(), anyString(), anyString()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Order result = orderService.updateOrderStatus(orderId, newStatus);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo(newStatus);
        
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
        verify(auditLogService).logOrderAction(
            eq(orderId),
            isNull(),
            eq("Status Updated"),
            contains("Status changed from " + oldStatus + " to " + newStatus)
        );
    }
    
    @Test
    @DisplayName("Test Order Confirmation Email Send - UT027")
    void testOrderConfirmationEmailSend() {
        // Given - Arrange test data
        Long orderId = 1L;
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);
        when(auditLogService.logOrderAction(anyLong(), any(), anyString(), anyString()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Order result = orderService.confirmOrder(orderId);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
        verify(auditLogService).logOrderAction(
            eq(orderId),
            isNull(),
            eq("Confirmed"),
            eq("Order confirmed")
        );
    }
    
    @Test
    @DisplayName("Test Place Order Valid Cart Items - UT028")
    void testPlaceOrderValidCartItems() {
        // Given - Arrange test data
        when(deliveryRepository.save(any(DeliveryInformation.class))).thenReturn(deliveryInfo);
        when(orderRepository.save(any(Order.class))).thenReturn(sampleOrder);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(new Invoice());
        when(auditLogService.logOrderAction(anyLong(), any(), anyString(), anyString()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Order result = orderService.createOrder(cartItems, deliveryInfo);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(result.getDeliveryInformation()).isEqualTo(deliveryInfo);
        
        verify(deliveryRepository).save(deliveryInfo);
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(sampleProduct);
        verify(invoiceRepository).save(any(Invoice.class));
        verify(auditLogService).logOrderAction(
            eq(1L),
            isNull(),
            eq("Created"),
            contains("Order created with 1 items")
        );
    }
    
    @Test
    @DisplayName("Test Order Creation With Invalid Cart Items Throws Exception")
    void testOrderCreationWithInvalidCartItemsThrowsException() {
        // Given - Arrange test data with empty cart
        List<OrderService.CartItem> emptyCartItems = new ArrayList<>();
        
        // When & Then - Act and assert exception
        assertThatThrownBy(() -> orderService.createOrder(emptyCartItems, deliveryInfo))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cart cannot be empty");
        
        verify(deliveryRepository, never()).save(any(DeliveryInformation.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Test Order Not Found Throws Exception")
    void testOrderNotFoundThrowsException() {
        // Given - Arrange test data
        Long nonExistentOrderId = 999L;
        
        when(orderRepository.findById(nonExistentOrderId)).thenReturn(Optional.empty());
        
        // When & Then - Act and assert exception
        assertThatThrownBy(() -> orderService.updateOrderStatus(nonExistentOrderId, Order.OrderStatus.PROCESSING))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Order not found with ID: " + nonExistentOrderId);
        
        verify(orderRepository).findById(nonExistentOrderId);
        verify(orderRepository, never()).save(any(Order.class));
    }
    
    @Test
    @DisplayName("Test Cancel Order Restores Stock")
    void testCancelOrderRestoresStock() {
        // Given - Arrange test data
        Long orderId = 1L;
        String reason = "Customer request";
        
        // Mock the order with original quantity to test stock restoration
        Product productWithStock = new Book();
        productWithStock.setProductId(1L);
        productWithStock.setQuantity(8); // Original 10 - 2 ordered
        
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(productWithStock);
        orderItem.setQuantity(2);
        
        Order orderToCancel = new Order();
        orderToCancel.setOrderId(orderId);
        orderToCancel.setStatus(Order.OrderStatus.PENDING);
        orderToCancel.setOrderItems(List.of(orderItem));
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderToCancel));
        when(orderRepository.save(any(Order.class))).thenReturn(orderToCancel);
        when(productRepository.save(any(Product.class))).thenReturn(productWithStock);
        when(auditLogService.logOrderAction(anyLong(), any(), anyString(), anyString()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Order result = orderService.cancelOrder(orderId, reason);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(orderId);
        
        verify(orderRepository).findById(orderId);
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(productWithStock); // Stock should be restored
        verify(auditLogService).logOrderAction(
            eq(orderId),
            isNull(),
            eq("Cancelled"),
            eq("Order cancelled: " + reason)
        );
    }
    
    @Test
    @DisplayName("Test Get Pending Orders")
    void testGetPendingOrders() {
        // Given - Arrange test data
        List<Order> pendingOrders = List.of(sampleOrder);
        
        when(orderRepository.findPendingOrders()).thenReturn(pendingOrders);
        
        // When - Act on the method under test
        List<Order> result = orderService.getPendingOrders();
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        
        verify(orderRepository).findPendingOrders();
    }
    
    @Test
    @DisplayName("Test Insufficient Stock Validation")
    void testInsufficientStockValidation() {
        // Given - Arrange test data with insufficient stock
        Product lowStockProduct = new Book();
        lowStockProduct.setProductId(1L);
        lowStockProduct.setTitle("Low Stock Book");
        lowStockProduct.setQuantity(1); // Only 1 in stock
        
        List<OrderService.CartItem> insufficientStockCart = List.of(
            new OrderService.CartItem(lowStockProduct, 5) // Requesting 5, but only 1 available
        );
        
        // When & Then - Act and assert exception
        assertThatThrownBy(() -> orderService.createOrder(insufficientStockCart, deliveryInfo))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Insufficient stock");
        
        verify(deliveryRepository, never()).save(any(DeliveryInformation.class));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
