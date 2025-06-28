package com.itss.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itss.ecommerce.dto.BookDTO;
import com.itss.ecommerce.dto.ProductDTO;
import com.itss.ecommerce.dto.mapper.ProductMapper;
import com.itss.ecommerce.entity.Book;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Book sampleBook;
    private ProductDTO productDTO;
    private BookDTO bookDTO;

    @BeforeEach
    void setUp() {
        // Sample Book
        sampleBook = new Book();
        sampleBook.setProductId(1L);
        sampleBook.setTitle("Sample Book");
        sampleBook.setPrice(150000);
        sampleBook.setQuantity(10);
        sampleBook.setAuthors("John Author");
        sampleBook.setGenre("Fiction");
        sampleBook.setPublishers("Test Publisher");
        sampleBook.setRushOrderSupported(true);

        // Sample ProductDTO
        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setTitle("Sample Book");
        productDTO.setPrice(150000);
        productDTO.setQuantity(10);
        productDTO.setType("book");
        productDTO.setRushOrderSupported(true);

        // Sample BookDTO
        bookDTO = new BookDTO();
        bookDTO.setProductId(1L);
        bookDTO.setTitle("Sample Book");
        bookDTO.setPrice(150000);
        bookDTO.setQuantity(10);
        bookDTO.setAuthors("John Author");
        bookDTO.setGenre("Fiction");
        bookDTO.setPublishers("Test Publisher");
        bookDTO.setRushOrderSupported(true);
    }

    @Test
    @DisplayName("Test Get All Products")
    void testGetAllProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(sampleBook);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);

        when(productService.getAllProducts()).thenReturn(products);
        when(productMapper.toDTOList(products)).thenReturn(productDTOs);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Sample Book"))
                .andExpect(jsonPath("$.data[0].price").value(150000));

        verify(productService).getAllProducts();
        verify(productMapper).toDTOList(products);
    }

    @Test
    @DisplayName("Test Get Product By ID - Success")
    void testGetProductByIdSuccess() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(Optional.of(sampleBook));
        when(productMapper.toDTO(sampleBook)).thenReturn(productDTO);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Sample Book"))
                .andExpect(jsonPath("$.data.productId").value(1));

        verify(productService).getProductById(1L);
        verify(productMapper).toDTO(sampleBook);
    }

    @Test
    @DisplayName("Test Get Product By ID - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        // Given
        when(productService.getProductById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpected(jsonPath("$.success").value(false))
                .andExpected(jsonPath("$.message").value("Product not found with ID: 999"));

        verify(productService).getProductById(999L);
        verify(productMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Test Search Products")
    void testSearchProducts() throws Exception {
        // Given
        List<Product> searchResults = Arrays.asList(sampleBook);
        List<ProductDTO> searchDTOs = Arrays.asList(productDTO);

        when(productService.searchProducts("Book", null, null, null, null))
                .thenReturn(searchResults);
        when(productMapper.toDTOList(searchResults)).thenReturn(searchDTOs);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                        .param("title", "Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Sample Book"));

        verify(productService).searchProducts("Book", null, null, null, null);
        verify(productMapper).toDTOList(searchResults);
    }

    @Test
    @DisplayName("Test Get Rush Order Products")
    void testGetRushOrderProducts() throws Exception {
        // Given
        List<Product> rushProducts = Arrays.asList(sampleBook);
        List<ProductDTO> rushDTOs = Arrays.asList(productDTO);

        when(productService.getRushOrderProducts()).thenReturn(rushProducts);
        when(productMapper.toDTOList(rushProducts)).thenReturn(rushDTOs);

        // When & Then
        mockMvc.perform(get("/api/products/rush-order"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].rushOrderSupported").value(true));

        verify(productService).getRushOrderProducts();
        verify(productMapper).toDTOList(rushProducts);
    }

    @Test
    @DisplayName("Test Create Book")
    void testCreateBook() throws Exception {
        // Given
        when(productMapper.toEntity(any(BookDTO.class))).thenReturn(sampleBook);
        when(productService.saveProduct(any(Book.class))).thenReturn(sampleBook);
        when(productMapper.toBookDTO(any(Book.class))).thenReturn(bookDTO);

        // When & Then
        mockMvc.perform(post("/api/products/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Sample Book"))
                .andExpect(jsonPath("$.data.authors").value("John Author"));

        verify(productMapper).toEntity(any(BookDTO.class));
        verify(productService).saveProduct(any(Book.class));
        verify(productMapper).toBookDTO(any(Book.class));
    }

    @Test
    @DisplayName("Test Update Product")
    void testUpdateProduct() throws Exception {
        // Given
        when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(sampleBook);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(sampleBook);
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);

        // When & Then
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Sample Book"));

        verify(productMapper).toEntity(any(ProductDTO.class));
        verify(productService).updateProduct(eq(1L), any(Product.class));
        verify(productMapper).toDTO(any(Product.class));
    }

    @Test
    @DisplayName("Test Delete Product")
    void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));

        verify(productService).deleteProduct(1L);
    }

    @Test
    @DisplayName("Test Update Product Stock")
    void testUpdateProductStock() throws Exception {
        // Given
        when(productService.updateStock(1L, 5)).thenReturn(sampleBook);
        when(productMapper.toDTO(sampleBook)).thenReturn(productDTO);

        // When & Then
        mockMvc.perform(patch("/api/products/1/stock")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(10));

        verify(productService).updateStock(1L, 5);
        verify(productMapper).toDTO(sampleBook);
    }

    @Test
    @DisplayName("Test Check Product Availability")
    void testCheckProductAvailability() throws Exception {
        // Given
        when(productService.checkAvailability(1L, 5)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/products/1/availability")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));

        verify(productService).checkAvailability(1L, 5);
    }

    @Test
    @DisplayName("Test Get Low Stock Products")
    void testGetLowStockProducts() throws Exception {
        // Given
        List<Product> lowStockProducts = Arrays.asList(sampleBook);
        List<ProductDTO> lowStockDTOs = Arrays.asList(productDTO);

        when(productService.getLowStockProducts(5)).thenReturn(lowStockProducts);
        when(productMapper.toDTOList(lowStockProducts)).thenReturn(lowStockDTOs);

        // When & Then
        mockMvc.perform(get("/api/products/low-stock")
                        .param("threshold", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(productService).getLowStockProducts(5);
        verify(productMapper).toDTOList(lowStockProducts);
    }

    @Test
    @DisplayName("Test Create Book with Invalid Data")
    void testCreateBookWithInvalidData() throws Exception {
        // Given - Invalid BookDTO with missing required fields
        BookDTO invalidBookDTO = new BookDTO();
        invalidBookDTO.setTitle(""); // Empty title
        invalidBookDTO.setPrice(-100); // Negative price

        // When & Then
        mockMvc.perform(post("/api/products/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBookDTO)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Test Invalid Product ID Path Variable")
    void testInvalidProductIdPathVariable() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products/invalid-id"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).getProductById(any());
    }
}
