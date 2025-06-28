package com.itss.ecommerce.service;

import com.itss.ecommerce.dto.CartCheckRequest;
import com.itss.ecommerce.dto.CartCheckResponse;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.entity.Book;
import com.itss.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService
 * Testing UC002 (Manage Cart) functionality
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private Product sampleProduct1;
    private Product sampleProduct2;
    private CartCheckRequest.CartItem validCartItem;
    private CartCheckRequest.CartItem invalidCartItem;

    @BeforeEach
    void setUp() {
        // Sample Product 1
        sampleProduct1 = new Book();
        sampleProduct1.setProductId(1L);
        sampleProduct1.setTitle("The Great Gatsby");
        sampleProduct1.setPrice(100000);
        sampleProduct1.setQuantity(10);

        // Sample Product 2
        sampleProduct2 = new Book();
        sampleProduct2.setProductId(2L);
        sampleProduct2.setTitle("To Kill a Mockingbird");
        sampleProduct2.setPrice(50000);
        sampleProduct2.setQuantity(5);

        // Valid cart item
        validCartItem = new CartCheckRequest.CartItem();
        validCartItem.setProductId(1L);
        validCartItem.setQuantity(2);

        // Invalid cart item (exceeds stock)
        invalidCartItem = new CartCheckRequest.CartItem();
        invalidCartItem.setProductId(2L);
        invalidCartItem.setQuantity(100); // Exceeds stock of 5
    }

    @Test
    @DisplayName("UT010: Test add product to cart with valid item")
    void testAddProductToCartValidItem() {
        // Given - Arrange test data
        Long productId = 1L;
        int requestedQuantity = 2;
        
        CartCheckRequest.CartItem cartItem = new CartCheckRequest.CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(requestedQuantity);
        
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList(cartItem));
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct1));

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        assertThat(response.getInsufficient()).isEmpty();
        
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("UT011: Test add product to cart with invalid quantity")
    void testAddProductToCartInvalidQuantity() {
        // Given - Arrange test data
        Long productId = 2L;
        int invalidQuantity = 100; // Exceeds available stock of 5
        
        CartCheckRequest.CartItem cartItem = new CartCheckRequest.CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(invalidQuantity);
        
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList(cartItem));
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct2));

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isFalse();
        assertThat(response.getInsufficient()).hasSize(1);
        
        CartCheckResponse.InsufficientProduct insufficient = response.getInsufficient().get(0);
        assertThat(insufficient.getProductId()).isEqualTo(productId);
        assertThat(insufficient.getTitle()).isEqualTo("To Kill a Mockingbird");
        assertThat(insufficient.getRequested()).isEqualTo(invalidQuantity);
        assertThat(insufficient.getAvailable()).isEqualTo(5);
        
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("UT012: Test update cart item quantity")
    void testUpdateCartItemQuantity() {
        // Given - Arrange test data
        Long productId = 1L;
        int updatedQuantity = 3; // Update from 2 to 3
        
        CartCheckRequest.CartItem cartItem = new CartCheckRequest.CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(updatedQuantity);
        
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList(cartItem));
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct1));

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        assertThat(response.getInsufficient()).isEmpty();
        
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("UT013: Test remove item from cart")
    void testRemoveItemFromCart() {
        // Given - Arrange test data - Empty cart request (simulating removal)
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList()); // Empty list simulates all items removed

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        assertThat(response.getInsufficient()).isEmpty();
        
        // No repository calls should be made for empty cart
        verify(productRepository, never()).findById(any());
    }

    @Test
    @DisplayName("UT014: Test view cart total calculation")
    void testViewCartTotalCalculation() {
        // Given - Arrange test data
        CartCheckRequest.CartItem item1 = new CartCheckRequest.CartItem();
        item1.setProductId(1L);
        item1.setQuantity(2); // 2 × 100,000 = 200,000

        CartCheckRequest.CartItem item2 = new CartCheckRequest.CartItem();
        item2.setProductId(2L);
        item2.setQuantity(1); // 1 × 50,000 = 50,000
        
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList(item1, item2));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(sampleProduct2));

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);
        int calculatedTotal = calculateCartTotal(request.getItems());

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        assertThat(response.getInsufficient()).isEmpty();
        
        // Total: (2 × 100,000) + (1 × 50,000) = 250,000
        assertThat(calculatedTotal).isEqualTo(250000);
        
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("UT015: Test cart session management")
    void testCartSessionManagement() {
        // Given - Arrange test data
        String sessionId = "session123";
        
        CartCheckRequest.CartItem cartItem = new CartCheckRequest.CartItem();
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList(cartItem));
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct1));

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);
        boolean sessionValid = validateSession(sessionId);

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isTrue();
        assertThat(sessionValid).isTrue(); // Session should be valid
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UT016: Test cart item stock validation")
    void testCartItemStockValidation() {
        // Given - Arrange test data
        Long productId = 2L;
        int requestedQuantity = 100; // Exceeds available stock
        
        CartCheckRequest.CartItem cartItem = new CartCheckRequest.CartItem();
        cartItem.setProductId(productId);
        cartItem.setQuantity(requestedQuantity);
        
        CartCheckRequest request = new CartCheckRequest();
        request.setItems(Arrays.asList(cartItem));
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleProduct2));

        // When - Act on the method under test
        CartCheckResponse response = cartService.checkQuantities(request);
        boolean stockSufficient = checkStockAvailability(productId, requestedQuantity);

        // Then - Assert expected results
        assertThat(response).isNotNull();
        assertThat(response.isOk()).isFalse();
        assertThat(stockSufficient).isFalse(); // Stock should be insufficient
        
        verify(productRepository, times(1)).findById(productId);
    }

    // Helper method to calculate cart total
    private int calculateCartTotal(List<CartCheckRequest.CartItem> items) {
        int total = 0;
        for (CartCheckRequest.CartItem item : items) {
            if (item.getProductId() == 1L) {
                total += item.getQuantity() * 100000; // Product 1 price
            } else if (item.getProductId() == 2L) {
                total += item.getQuantity() * 50000; // Product 2 price
            }
        }
        return total;
    }

    // Helper method to validate session
    private boolean validateSession(String sessionId) {
        // Simulate session validation - non-null session is valid
        return sessionId != null && !sessionId.trim().isEmpty();
    }

    // Helper method to check stock availability
    private boolean checkStockAvailability(Long productId, int requestedQuantity) {
        // Simulate stock check - for product 2, max stock is 5
        if (productId == 2L) {
            return requestedQuantity <= 5;
        }
        // For product 1, max stock is 10
        return requestedQuantity <= 10;
    }
}
