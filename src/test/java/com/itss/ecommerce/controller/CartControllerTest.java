package com.itss.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itss.ecommerce.dto.CartCheckRequest;
import com.itss.ecommerce.dto.CartCheckResponse;
import com.itss.ecommerce.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private CartCheckRequest cartCheckRequest;
    private CartCheckResponse successResponse;
    private CartCheckResponse insufficientStockResponse;

    @BeforeEach
    void setUp() {
        // Sample Cart Check Request
        CartCheckRequest.CartItem cartItem1 = new CartCheckRequest.CartItem();
        cartItem1.setProductId(1L);
        cartItem1.setQuantity(2);

        CartCheckRequest.CartItem cartItem2 = new CartCheckRequest.CartItem();
        cartItem2.setProductId(2L);
        cartItem2.setQuantity(1);

        cartCheckRequest = new CartCheckRequest();
        cartCheckRequest.setItems(Arrays.asList(cartItem1, cartItem2));

        // Success Response
        successResponse = new CartCheckResponse(true, Collections.emptyList());

        // Insufficient Stock Response
        CartCheckResponse.InsufficientProduct insufficientProduct = 
            new CartCheckResponse.InsufficientProduct(1L, "Sample Book", 5, 3);
        
        insufficientStockResponse = new CartCheckResponse(false, Arrays.asList(insufficientProduct));
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Success")
    void testCheckCartQuantitiesSuccess() throws Exception {
        // Given
        when(cartService.checkQuantities(any(CartCheckRequest.class))).thenReturn(successResponse);

        // When & Then
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartCheckRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ok").value(true))
                .andExpect(jsonPath("$.data.insufficient").isEmpty());

        verify(cartService).checkQuantities(any(CartCheckRequest.class));
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Insufficient Stock")
    void testCheckCartQuantitiesInsufficientStock() throws Exception {
        // Given
        when(cartService.checkQuantities(any(CartCheckRequest.class))).thenReturn(insufficientStockResponse);

        // When & Then
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartCheckRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ok").value(false))
                .andExpect(jsonPath("$.data.insufficient").isArray())
                .andExpect(jsonPath("$.data.insufficient[0].productId").value(1))
                .andExpect(jsonPath("$.data.insufficient[0].title").value("Sample Book"))
                .andExpect(jsonPath("$.data.insufficient[0].requested").value(5))
                .andExpect(jsonPath("$.data.insufficient[0].available").value(3));

        verify(cartService).checkQuantities(any(CartCheckRequest.class));
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Empty Cart")
    void testCheckCartQuantitiesEmptyCart() throws Exception {
        // Given
        CartCheckRequest emptyRequest = new CartCheckRequest();
        emptyRequest.setItems(Collections.emptyList());

        CartCheckResponse emptyResponse = new CartCheckResponse(true, Collections.emptyList());

        when(cartService.checkQuantities(any(CartCheckRequest.class))).thenReturn(emptyResponse);

        // When & Then
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.ok").value(true));

        verify(cartService).checkQuantities(any(CartCheckRequest.class));
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Invalid Request")
    void testCheckCartQuantitiesInvalidRequest() throws Exception {
        // When & Then - Sending invalid JSON
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).checkQuantities(any());
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Null Cart Items")
    void testCheckCartQuantitiesNullItems() throws Exception {
        // Given
        CartCheckRequest nullItemsRequest = new CartCheckRequest();
        nullItemsRequest.setItems(null);

        // When & Then - This should result in a validation error
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullItemsRequest)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).checkQuantities(any());
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Invalid Product ID")
    void testCheckCartQuantitiesInvalidProductId() throws Exception {
        // Given
        CartCheckRequest.CartItem invalidItem = new CartCheckRequest.CartItem();
        invalidItem.setProductId(-1L); // Invalid negative ID
        invalidItem.setQuantity(1);

        CartCheckRequest invalidRequest = new CartCheckRequest();
        invalidRequest.setItems(Arrays.asList(invalidItem));

        // When & Then
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).checkQuantities(any());
    }

    @Test
    @DisplayName("Test Check Cart Quantities - Invalid Quantity")
    void testCheckCartQuantitiesInvalidQuantity() throws Exception {
        // Given
        CartCheckRequest.CartItem invalidItem = new CartCheckRequest.CartItem();
        invalidItem.setProductId(1L);
        invalidItem.setQuantity(0); // Invalid zero quantity

        CartCheckRequest invalidRequest = new CartCheckRequest();
        invalidRequest.setItems(Arrays.asList(invalidItem));

        // When & Then
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).checkQuantities(any());
    }

    @Test
    @DisplayName("Test Cart Service Exception Handling")
    void testCartServiceExceptionHandling() throws Exception {
        // Given
        when(cartService.checkQuantities(any(CartCheckRequest.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        mockMvc.perform(post("/api/cart/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartCheckRequest)))
                .andExpect(status().isInternalServerError());

        verify(cartService).checkQuantities(any(CartCheckRequest.class));
    }
}
