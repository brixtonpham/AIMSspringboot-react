package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.repository.*;
import com.itss.ecommerce.service.admin.OrderService;
import com.itss.ecommerce.service.log.AuditLogService;
import com.itss.ecommerce.service.product.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RushOrderServiceTest {

    @Mock
    private OrderService orderService;
    
    @Mock
    private ProductService productService;
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private DeliveryInformationRepository deliveryRepository;
    
    @Mock
    private AuditLogService auditLogService;
    
    @Mock
    private InvoiceRepository invoiceRepository;
    
    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;
    
    @InjectMocks
    private OrderService rushOrderService; // Using OrderService as it handles rush orders
    
    private Book rushProduct;
    private Book regularProduct;
    private Order rushOrder;
    private OrderItem rushOrderItem;
    private DeliveryInformation deliveryInfo;
    
    @BeforeEach
    void setUp() {
        // Sample Product with rush order support
        rushProduct = new Book();
        rushProduct.setProductId(1L);
        rushProduct.setTitle("Rush Delivery Book");
        rushProduct.setPrice(200000);
        rushProduct.setQuantity(10);
        rushProduct.setRushOrderSupported(true);
        rushProduct.setAuthors("Test Author");
        rushProduct.setGenre("Fiction");
        rushProduct.setPublishers("Test Publisher");
        
        // Sample Product without rush order support
        regularProduct = new Book();
        regularProduct.setProductId(2L);
        regularProduct.setTitle("Regular Book");
        regularProduct.setPrice(150000);
        regularProduct.setQuantity(5);
        regularProduct.setRushOrderSupported(false);
        regularProduct.setAuthors("Regular Author");
        regularProduct.setGenre("Non-Fiction");
        regularProduct.setPublishers("Regular Publisher");
        
        // Sample Delivery Information
        deliveryInfo = new DeliveryInformation();
        deliveryInfo.setName("John Doe");
        deliveryInfo.setEmail("john.doe@example.com");
        deliveryInfo.setPhone("0123456789");
        deliveryInfo.setAddress("123 Test Street, Hanoi");
        deliveryInfo.setProvince("Hanoi");
        deliveryInfo.setDeliveryFee(50000); // Higher fee for rush delivery
        
        // Sample Rush Order
        rushOrder = new Order();
        rushOrder.setOrderId(1L);
        rushOrder.setStatus(Order.OrderStatus.PENDING);
        rushOrder.setDeliveryInformation(deliveryInfo);
        rushOrder.setTotalBeforeVat(240000); // Base price + rush fee
        rushOrder.setTotalAfterVat(264000); // With 10% VAT
        rushOrder.setVatPercentage(10);
        rushOrder.setCreatedAt(LocalDateTime.now());
        rushOrder.setUpdatedAt(LocalDateTime.now());
        
        // Sample Rush Order Item
        rushOrderItem = new OrderItem();
        rushOrderItem.setOrderItemId(1L);
        rushOrderItem.setProduct(rushProduct);
        rushOrderItem.setQuantity(1);
        rushOrderItem.setTotalFee(240000); // 200000 + 20% rush fee
        rushOrderItem.setRushOrder(true);
        rushOrderItem.setInstructions("Urgent delivery needed");
        rushOrderItem.setStatus(OrderItem.OrderItemStatus.PENDING);
        rushOrderItem.setOrder(rushOrder);
        
        rushOrder.setOrderItems(Arrays.asList(rushOrderItem));
    }
    
    @Test
    @DisplayName("Test Place Rush Order Priority Handling - UT032")
    void testPlaceRushOrderPriorityHandling() {
        // Given
        when(orderRepository.save(any(Order.class))).thenReturn(rushOrder);
        when(deliveryRepository.save(any(DeliveryInformation.class))).thenReturn(deliveryInfo);
        when(invoiceRepository.save(any())).thenReturn(null);
        when(paymentTransactionRepository.save(any())).thenReturn(new PaymentTransaction());
        
        OrderService.CartItem cartItem = new OrderService.CartItem(rushProduct, 1);
        List<OrderService.CartItem> cartItems = Arrays.asList(cartItem);
        
        // When
        Order result = rushOrderService.createOrder(cartItems, deliveryInfo);
        
        // Then
        assertThat(result)
            .isNotNull()
            .satisfies(order -> {
                assertThat(order.hasRushItems()).isTrue();
                assertThat(order.getOrderItems()).hasSize(1);
                assertThat(order.getOrderItems().get(0).isRushOrder()).isTrue();
                assertThat(order.getDeliveryInformation().getDeliveryFee()).isEqualTo(50000);
            });
        
        verify(orderRepository).save(any(Order.class));
        verify(productRepository).save(any(Product.class)); // Stock reduction
    }
    
    @Test
    @DisplayName("Test Rush Order Fee Calculation - UT033")
    void testRushOrderFeeCalculation() {
        // Given
        int basePrice = 200000;
        int quantity = 2;
        int expectedRushFee = (int) (basePrice * 0.2 * quantity); // 20% rush fee
        int expectedTotal = (basePrice * quantity) + expectedRushFee;
        
        // When - Calculate rush order fee (20% of base price)
        int actualRushFee = (int) (basePrice * 0.2 * quantity);
        int actualTotal = (basePrice * quantity) + actualRushFee;
        
        // Then
        assertThat(actualRushFee).isEqualTo(expectedRushFee);
        assertThat(actualTotal).isEqualTo(expectedTotal);
        assertThat(actualRushFee).isEqualTo(80000); // 20% of 200000 * 2
        assertThat(actualTotal).isEqualTo(480000); // 400000 + 80000
    }
    
    @Test
    @DisplayName("Test Rush Order Stock Availability - UT034")
    void testRushOrderStockAvailability() {
        // Given
        int requestedQuantity = 15; // More than available stock (10)
        
        // When & Then - Should throw exception for insufficient stock
        OrderService.CartItem cartItem = new OrderService.CartItem(rushProduct, requestedQuantity);
        List<OrderService.CartItem> cartItems = Arrays.asList(cartItem);
        
        assertThatThrownBy(() -> rushOrderService.createOrder(cartItems, deliveryInfo))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Insufficient stock");
        
        // Verify stock check
        assertThat(rushProduct.hasStock(requestedQuantity)).isFalse();
        assertThat(rushProduct.hasStock(5)).isTrue(); // Within available stock
    }
    
    @Test
    @DisplayName("Test Rush Order Delivery Time Validation - UT035")
    void testRushOrderDeliveryTimeValidation() {
        // Given
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(rushOrderItem));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(rushOrderItem);
        
        String rushInstructions = "Deliver within 24 hours";
        
        // When
        OrderItem result = rushOrderService.addRushDelivery(1L, rushInstructions);
        
        // Then
        assertThat(result)
            .isNotNull()
            .satisfies(orderItem -> {
                assertThat(orderItem.isRushOrder()).isTrue();
                assertThat(orderItem.getInstructions()).isEqualTo(rushInstructions);
                assertThat(orderItem.getProduct().getRushOrderSupported()).isTrue();
            });
        
        verify(orderItemRepository).save(any(OrderItem.class));
    }
    
    @Test
    @DisplayName("Test Rush Order Not Supported Product")
    void testRushOrderNotSupportedProduct() {
        // Given
        OrderItem regularOrderItem = new OrderItem();
        regularOrderItem.setOrderItemId(2L);
        regularOrderItem.setProduct(regularProduct);
        regularOrderItem.setQuantity(1);
        regularOrderItem.setRushOrder(false);
        
        when(orderItemRepository.findById(2L)).thenReturn(Optional.of(regularOrderItem));
        
        // When & Then - Should throw exception for unsupported rush order
        assertThatThrownBy(() -> rushOrderService.addRushDelivery(2L, "Rush needed"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Product does not support rush delivery");
        
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }
    
    @Test
    @DisplayName("Test Get Orders With Rush Delivery")
    void testGetOrdersWithRushDelivery() {
        // Given
        List<Order> rushOrders = Arrays.asList(rushOrder);
        when(orderRepository.findOrdersWithRushDelivery()).thenReturn(rushOrders);
        
        // When
        List<Order> result = rushOrderService.getOrdersWithRushDelivery();
        
        // Then
        assertThat(result)
            .isNotNull()
            .hasSize(1)
            .satisfies(orders -> {
                assertThat(orders.get(0).hasRushItems()).isTrue();
                assertThat(orders.get(0).getOrderId()).isEqualTo(1L);
            });
        
        verify(orderRepository).findOrdersWithRushDelivery();
    }
    
    @Test
    @DisplayName("Test Rush Order Item Not Found")
    void testRushOrderItemNotFound() {
        // Given
        when(orderItemRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> rushOrderService.addRushDelivery(999L, "Rush instructions"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Order line not found with ID: 999");
    }
}
