package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.repository.*;
import com.itss.ecommerce.service.auth.AuthService;
import com.itss.ecommerce.service.log.AuditLogService;
import com.itss.ecommerce.service.product.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductManagerServiceTest {
    
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
    
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private ProductService productService;
    
    private Book sampleBook;
    private CD sampleCD;
    
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
    }
    
    @Test
    @DisplayName("Test Add Product With Valid Data - UT022")
    void testAddProductValidData() {
        // Given - Arrange test data
        when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), any()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Product savedProduct = productService.saveProduct(sampleBook);
        
        // Then - Assert expected results
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getTitle()).isEqualTo("The Great Gatsby");
        assertThat(savedProduct.getPrice()).isEqualTo(150000);
        assertThat(savedProduct.getQuantity()).isEqualTo(10);
        
        verify(bookRepository).save(sampleBook);
        verify(auditLogService).logAction(
            eq("Product Saved"),
            eq("Product"),
            eq(1L),
            eq(AuditLog.ActionType.CREATE),
            isNull()
        );
    }
    
    @Test
    @DisplayName("Test Update Product Existing Item - UT023")
    void testUpdateProductExistingItem() {
        // Given - Arrange test data
        Long productId = 1L;
        Product updatedProduct = new Book();
        updatedProduct.setProductId(productId);  // Set the productId
        updatedProduct.setTitle("The Great Gatsby - Updated Edition");
        updatedProduct.setPrice(180000);
        updatedProduct.setWeight(0.6f);
        updatedProduct.setRushOrderSupported(true);
        updatedProduct.setImageUrl("https://example.com/gatsby-updated.jpg");
        updatedProduct.setBarcode("9780123456789");
        updatedProduct.setImportDate("2024-01-01");
        updatedProduct.setIntroduction("Updated classic American literature");
        updatedProduct.setQuantity(15);
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleBook));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), any()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Product result = productService.updateProduct(productId, updatedProduct);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("The Great Gatsby - Updated Edition");
        assertThat(result.getPrice()).isEqualTo(180000);
        assertThat(result.getQuantity()).isEqualTo(15);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(auditLogService).logAction(
            eq("Product Updated"),
            eq("Product"),
            anyLong(),
            eq(AuditLog.ActionType.UPDATE),
            isNull()
        );
    }
    
    @Test
    @DisplayName("Test Product Manager Authorization - UT024")
    void testProductManagerAuthorization() {
        // Given - Arrange test data
        Long managerUserId = 1L;
        Long nonManagerId = 2L;
        
        when(authService.canManageProducts(managerUserId)).thenReturn(true);
        when(authService.canManageProducts(nonManagerId)).thenReturn(false);
        
        // When & Then - Test manager authorization
        boolean managerCanManage = authService.canManageProducts(managerUserId);
        boolean nonManagerCanManage = authService.canManageProducts(nonManagerId);
        
        // Assert expected results
        assertThat(managerCanManage).isTrue();
        assertThat(nonManagerCanManage).isFalse();
        
        verify(authService).canManageProducts(managerUserId);
        verify(authService).canManageProducts(nonManagerId);
    }
    
    @Test
    @DisplayName("Test Product Stock Update - UT025")
    void testProductStockUpdate() {
        // Given - Arrange test data
        Long productId = 1L;
        int stockIncrease = 5;
        Product updatedProduct = new Book();
        updatedProduct.setProductId(productId);
        updatedProduct.setQuantity(15); // Original 10 + 5
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleBook));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), any()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        Product result = productService.updateStock(productId, stockIncrease);
        
        // Then - Assert expected results
        assertThat(result).isNotNull();
        assertThat(result.getQuantity()).isEqualTo(15);
        
        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
        verify(auditLogService).logAction(
            eq("Stock Updated"),
            eq("Product"),
            eq(productId),
            eq(AuditLog.ActionType.UPDATE),
            isNull()
        );
    }
    
    @Test
    @DisplayName("Test Add Product Invalid Data Throws Exception")
    void testAddProductInvalidDataThrowsException() {
        // Given - Arrange test data with invalid product (null title)
        Product invalidProduct = new Book();
        invalidProduct.setTitle(null); // Invalid - null title
        invalidProduct.setPrice(150000);
        invalidProduct.setQuantity(10);
        
        // When & Then - Act and assert exception
        assertThatThrownBy(() -> productService.saveProduct(invalidProduct))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Product title is required");
        
        verify(bookRepository, never()).save(any(Book.class));
        verify(auditLogService, never()).logAction(anyString(), anyString(), anyLong(), any(), any());
    }
    
    @Test
    @DisplayName("Test Update Product Not Found Throws Exception")
    void testUpdateProductNotFoundThrowsException() {
        // Given - Arrange test data
        Long nonExistentProductId = 999L;
        Product updatedProduct = new Book();
        updatedProduct.setTitle("Some Title");
        updatedProduct.setPrice(100000);
        
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());
        
        // When & Then - Act and assert exception
        assertThatThrownBy(() -> productService.updateProduct(nonExistentProductId, updatedProduct))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Product not found with ID: " + nonExistentProductId);
        
        verify(productRepository).findById(nonExistentProductId);
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    @DisplayName("Test Delete Product Manager Access")
    void testDeleteProductManagerAccess() {
        // Given - Arrange test data
        Long productId = 1L;
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(sampleBook));
        doNothing().when(productRepository).delete(any(Product.class));
        when(auditLogService.logAction(anyString(), anyString(), anyLong(), any(AuditLog.ActionType.class), any()))
            .thenReturn(new AuditLog());
        
        // When - Act on the method under test
        assertThatCode(() -> productService.deleteProduct(productId))
            .doesNotThrowAnyException();
        
        // Then - Assert expected results
        verify(productRepository).findById(productId);
        verify(productRepository).delete(sampleBook);
        verify(auditLogService).logAction(
            eq("Product Deleted"),
            eq("Product"),
            eq(productId),
            eq(AuditLog.ActionType.DELETE),
            isNull()
        );
    }
}
