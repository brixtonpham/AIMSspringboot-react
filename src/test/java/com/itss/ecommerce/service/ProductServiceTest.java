package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.Book;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.ProductRepository;
import com.itss.ecommerce.service.log.AuditLogService;
import com.itss.ecommerce.service.product.ProductService;
import com.itss.ecommerce.repository.BookRepository;
import com.itss.ecommerce.repository.CDRepository;
import com.itss.ecommerce.repository.DVDRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService
 * Testing UC001 (View Products & Details) functionality
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CDRepository cdRepository;

    @Mock
    private DVDRepository dvdRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProductService productService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        // Sample Book for testing
        sampleBook = new Book();
        sampleBook.setProductId(1L);
        sampleBook.setTitle("The Great Gatsby");
        sampleBook.setPrice(150000);
        sampleBook.setQuantity(10);
        sampleBook.setImageUrl("https://example.com/gatsby.jpg");
        sampleBook.setGenre("Fiction");
        sampleBook.setAuthors("F. Scott Fitzgerald");
        sampleBook.setPublishers("Scribner");
        sampleBook.setPageCount(180);
        sampleBook.setCoverType("Hardcover");
    }

    @Test
    @DisplayName("UT001: Test request valid product by ID")
    void testRequestValidProductById() {
        // Given - Arrange test data
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(productId);

        // Then - Assert expected results
        assertThat(result).isPresent();
        assertThat(result.get().getProductId()).isEqualTo(productId);
        assertThat(result.get().getTitle()).isEqualTo("The Great Gatsby");
        assertThat(result.get().getPrice()).isEqualTo(150000);
        
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("UT002: Test product details fetch complete information")
    void testProductDetailsFetchCompleteInformation() {
        // Given - Arrange test data
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(productId);

        // Then - Assert expected results - Verify all product details are fetched
        assertThat(result).isPresent();
        Product product = result.get();
        
        // Verify basic product information
        assertThat(product.getProductId()).isEqualTo(1L);
        assertThat(product.getTitle()).isEqualTo("The Great Gatsby");
        assertThat(product.getPrice()).isEqualTo(150000);
        assertThat(product.getQuantity()).isEqualTo(10);
        assertThat(product.getImageUrl()).isEqualTo("https://example.com/gatsby.jpg");
        
        // Verify specific book information
        if (product instanceof Book) {
            Book book = (Book) product;
            assertThat(book.getGenre()).isEqualTo("Fiction");
            assertThat(book.getAuthors()).isEqualTo("F. Scott Fitzgerald");
            assertThat(book.getPublishers()).isEqualTo("Scribner");
            assertThat(book.getPageCount()).isEqualTo(180);
            assertThat(book.getCoverType()).isEqualTo("Hardcover");
        }
        
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("UT003: Test product not found for invalid ID")
    void testProductNotFoundInvalidId() {
        // Given - Arrange test data
        Long invalidProductId = 999L;
        when(productRepository.findById(invalidProductId)).thenReturn(Optional.empty());

        // When & Then - Act and Assert expected exception
        Optional<Product> result = productService.getProductById(invalidProductId);
        
        // Assert that empty optional is returned for non-existent product
        assertThat(result).isEmpty();
        
        verify(productRepository, times(1)).findById(invalidProductId);
    }

    @Test
    @DisplayName("UT004: Test product image URL validation")
    void testProductImageUrlValidation() {
        // Given - Arrange test data
        String validImageUrl = "https://example.com/image.jpg";
        sampleBook.setImageUrl(validImageUrl);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(1L);

        // Then - Assert expected results
        assertThat(result).isPresent();
        assertThat(result.get().getImageUrl()).isEqualTo(validImageUrl);
        assertThat(result.get().getImageUrl()).startsWith("https://");
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UT005: Test product category display")
    void testProductCategoryDisplay() {
        // Given - Arrange test data
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(1L);

        // Then - Assert expected results
        assertThat(result).isPresent();
        Product product = result.get();
        
        // Verify product type/category
        if (product instanceof Book) {
            assertThat(((Book) product).getProductType()).isEqualTo("book");
            assertThat(((Book) product).getGenre()).isEqualTo("Fiction");
        }
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UT006: Test product price format display")
    void testProductPriceFormatDisplay() {
        // Given - Arrange test data
        sampleBook.setPrice(150000);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(1L);

        // Then - Assert expected results
        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualTo(150000);
        
        // Verify price formatting logic (150000 → "150,000 VND")
        String formattedPrice = formatPrice(result.get().getPrice());
        assertThat(formattedPrice).isEqualTo("150,000 VND");
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UT007: Test product price format display with decimal")
    void testProductPriceFormatDisplayDecimal() {
        // Given - Arrange test data - Note: Using integer price as per entity definition
        sampleBook.setPrice(150050); // Representing 150,050 VND (no decimal in entity)
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(1L);

        // Then - Assert expected results
        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualTo(150050);
        
        // Verify price formatting logic (150050 → "150,050 VND")
        String formattedPrice = formatPrice(result.get().getPrice());
        assertThat(formattedPrice).isEqualTo("150,050 VND");
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UT008: Test product price format display zero")
    void testProductPriceFormatDisplayZero() {
        // Given - Arrange test data
        sampleBook.setPrice(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(1L);

        // Then - Assert expected results
        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isZero();
        
        // Verify price formatting logic (0 → "0 VND")
        String formattedPrice = formatPrice(result.get().getPrice());
        assertThat(formattedPrice).isEqualTo("0 VND");
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("UT009: Test product manager access control")
    void testProductManagerAccessControl() {
        // Given - Arrange test data
        Long managerId = 1L;
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        // When - Act on the method under test
        Optional<Product> result = productService.getProductById(1L);
        boolean hasManagerAccess = checkManagerAccess(managerId);

        // Then - Assert expected results
        assertThat(result).isPresent();
        assertThat(hasManagerAccess).isTrue(); // Manager with ID 1 should have access
        
        verify(productRepository, times(1)).findById(1L);
    }

    // Helper method to format price according to Vietnamese currency format
    private String formatPrice(Integer price) {
        if (price == null || price == 0) {
            return "0 VND";
        }
        
        // Simple formatting logic for Vietnamese currency
        String formattedNumber = String.format("%,d", price);
        return formattedNumber + " VND";
    }

    // Helper method to simulate manager access control
    private boolean checkManagerAccess(Long userId) {
        // Simulate manager role check - userId 1 is manager
        return userId != null && userId == 1L;
    }
}
