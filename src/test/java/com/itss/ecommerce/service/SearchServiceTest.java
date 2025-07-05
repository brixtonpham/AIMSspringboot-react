package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.repository.*;
import com.itss.ecommerce.service.admin.ProductService;
import com.itss.ecommerce.service.log.AuditLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {
    
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
    
    private List<Product> sampleProducts;
    private Book sampleBook;
    private CD sampleCD;
    private DVD sampleDVD;
    
    @BeforeEach
    void setUp() {
        // Sample Book
        sampleBook = new Book();
        sampleBook.setProductId(1L);
        sampleBook.setTitle("The Great Gatsby");
        sampleBook.setPrice(150000);
        sampleBook.setQuantity(10);
        sampleBook.setWeight(0.5f);
        sampleBook.setRushOrderSupported(true);
        sampleBook.setImageUrl("https://example.com/gatsby.jpg");
        sampleBook.setBarcode("9780123456789");
        sampleBook.setImportDate("2024-01-01");
        sampleBook.setIntroduction("Classic American literature");
        
        // Sample CD
        sampleCD = new CD();
        sampleCD.setProductId(2L);
        sampleCD.setTitle("Dark Side of the Moon");
        sampleCD.setPrice(250000);
        sampleCD.setQuantity(5);
        sampleCD.setWeight(0.2f);
        sampleCD.setRushOrderSupported(false);
        sampleCD.setImageUrl("https://example.com/darkside.jpg");
        sampleCD.setBarcode("1234567890123");
        sampleCD.setImportDate("2024-01-01");
        sampleCD.setIntroduction("Pink Floyd masterpiece");
        
        // Sample DVD
        sampleDVD = new DVD();
        sampleDVD.setProductId(3L);
        sampleDVD.setTitle("Inception");
        sampleDVD.setPrice(180000);
        sampleDVD.setQuantity(8);
        sampleDVD.setWeight(0.1f);
        sampleDVD.setRushOrderSupported(true);
        sampleDVD.setImageUrl("https://example.com/inception.jpg");
        sampleDVD.setBarcode("9876543210987");
        sampleDVD.setImportDate("2024-01-01");
        sampleDVD.setIntroduction("Mind-bending thriller");
        
        sampleProducts = Arrays.asList(sampleBook, sampleCD, sampleDVD);
    }
    
    @Test
    @DisplayName("Test Search Product By Title - UT029")
    void testSearchProductByTitle() {
        // Given - Arrange test data
        String searchTitle = "Great";
        List<Product> expectedResults = Arrays.asList(sampleBook);
        
        when(productRepository.searchProducts(eq(searchTitle), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(expectedResults);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(searchTitle, null, null, null, null);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("Great");
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        
        verify(productRepository).searchProducts(searchTitle, null, null, null, null);
    }
    
    @Test
    @DisplayName("Test Search Product By Category - UT030")
    void testSearchProductByCategory() {
        // Given - Arrange test data
        String categoryType = "cd";
        List<Product> expectedResults = Arrays.asList(sampleCD);
        
        when(productRepository.searchProducts(isNull(), eq(categoryType), isNull(), isNull(), isNull()))
            .thenReturn(expectedResults);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(null, categoryType, null, null, null);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(CD.class);
        assertThat(result.get(0).getTitle()).isEqualTo("Dark Side of the Moon");
        
        verify(productRepository).searchProducts(null, categoryType, null, null, null);
    }
    
    @Test
    @DisplayName("Test Search Product Price Filter - UT031")
    void testSearchProductPriceFilter() {
        // Given - Arrange test data
        Integer minPrice = 100000;
        Integer maxPrice = 200000;
        List<Product> expectedResults = Arrays.asList(sampleBook, sampleDVD); // 150000 and 180000
        
        when(productRepository.searchProducts(isNull(), isNull(), eq(minPrice), eq(maxPrice), isNull()))
            .thenReturn(expectedResults);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(null, null, minPrice, maxPrice, null);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).allSatisfy(product -> {
            assertThat(product.getPrice()).isBetween(minPrice, maxPrice);
        });
        
        verify(productRepository).searchProducts(null, null, minPrice, maxPrice, null);
    }
    
    @Test
    @DisplayName("Test Search Product With Multiple Criteria")
    void testSearchProductWithMultipleCriteria() {
        // Given - Arrange test data
        String title = "Inception";
        String type = "dvd";
        Integer minPrice = 150000;
        Integer maxPrice = 200000;
        Boolean inStock = true;
        
        List<Product> expectedResults = Arrays.asList(sampleDVD);
        
        when(productRepository.searchProducts(eq(title), eq(type), eq(minPrice), eq(maxPrice), eq(inStock)))
            .thenReturn(expectedResults);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(title, type, minPrice, maxPrice, inStock);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Inception");
        assertThat(result.get(0)).isInstanceOf(DVD.class);
        assertThat(result.get(0).getPrice()).isBetween(minPrice, maxPrice);
        assertThat(result.get(0).getQuantity()).isGreaterThan(0);
        
        verify(productRepository).searchProducts(title, type, minPrice, maxPrice, inStock);
    }
    
    @Test
    @DisplayName("Test Search Product No Results")
    void testSearchProductNoResults() {
        // Given - Arrange test data
        String nonExistentTitle = "NonExistentProduct";
        List<Product> emptyResults = Arrays.asList();
        
        when(productRepository.searchProducts(eq(nonExistentTitle), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(emptyResults);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(nonExistentTitle, null, null, null, null);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(productRepository).searchProducts(nonExistentTitle, null, null, null, null);
    }
    
    @Test
    @DisplayName("Test Search Product In Stock Filter")
    void testSearchProductInStockFilter() {
        // Given - Arrange test data
        Boolean inStock = true;
        List<Product> inStockProducts = Arrays.asList(sampleBook, sampleCD, sampleDVD);
        
        when(productRepository.searchProducts(isNull(), isNull(), isNull(), isNull(), eq(inStock)))
            .thenReturn(inStockProducts);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(null, null, null, null, inStock);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).allSatisfy(product -> {
            assertThat(product.getQuantity()).isGreaterThan(0);
        });
        
        verify(productRepository).searchProducts(null, null, null, null, inStock);
    }
    
    @Test
    @DisplayName("Test Search Product Out Of Stock Filter")
    void testSearchProductOutOfStockFilter() {
        // Given - Arrange test data
        Boolean inStock = false;
        
        // Create out of stock product
        Book outOfStockBook = new Book();
        outOfStockBook.setProductId(4L);
        outOfStockBook.setTitle("Out of Stock Book");
        outOfStockBook.setPrice(100000);
        outOfStockBook.setQuantity(0);
        
        List<Product> outOfStockProducts = Arrays.asList(outOfStockBook);
        
        when(productRepository.searchProducts(isNull(), isNull(), isNull(), isNull(), eq(inStock)))
            .thenReturn(outOfStockProducts);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(null, null, null, null, inStock);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result).allSatisfy(product -> {
            assertThat(product.getQuantity()).isEqualTo(0);
        });
        
        verify(productRepository).searchProducts(null, null, null, null, inStock);
    }
    
    @Test
    @DisplayName("Test Search Product Case Insensitive Title")
    void testSearchProductCaseInsensitiveTitle() {
        // Given - Arrange test data
        String lowercaseTitle = "gatsby";
        List<Product> expectedResults = Arrays.asList(sampleBook);
        
        when(productRepository.searchProducts(eq(lowercaseTitle), isNull(), isNull(), isNull(), isNull()))
            .thenReturn(expectedResults);
        
        // When - Act on the method under test
        List<Product> result = productService.searchProducts(lowercaseTitle, null, null, null, null);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle().toLowerCase()).contains(lowercaseTitle);
        
        verify(productRepository).searchProducts(lowercaseTitle, null, null, null, null);
    }
}
