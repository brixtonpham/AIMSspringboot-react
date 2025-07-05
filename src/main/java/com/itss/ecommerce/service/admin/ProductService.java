package com.itss.ecommerce.service.admin;

import com.itss.ecommerce.entity.AuditLog;
import com.itss.ecommerce.entity.Book;
import com.itss.ecommerce.entity.CD;
import com.itss.ecommerce.entity.DVD;
import com.itss.ecommerce.entity.LP;
import com.itss.ecommerce.entity.Product;
import com.itss.ecommerce.repository.BookRepository;
import com.itss.ecommerce.repository.CDRepository;
import com.itss.ecommerce.repository.DVDRepository;
import com.itss.ecommerce.repository.LPRepository;
import com.itss.ecommerce.repository.ProductRepository;
import com.itss.ecommerce.service.handler.ProductTypeHandler;
import com.itss.ecommerce.service.log.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final BookRepository bookRepository;
    private final CDRepository cdRepository;
    private final DVDRepository dvdRepository;
    private final LPRepository lpRepository;
    private final AuditLogService auditLogService;
    private final List<ProductTypeHandler> productTypeHandlers;
    
    /**
     * Get all products with type-specific data
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products with type-specific data");
        
        List<Product> allProducts = new java.util.ArrayList<>();
        
        // Fetch all specific product types to get complete data
        allProducts.addAll(bookRepository.findAll());
        allProducts.addAll(cdRepository.findAll());
        allProducts.addAll(dvdRepository.findAll());
        allProducts.addAll(lpRepository.findAll());
        
        // Sort by product ID to maintain consistent ordering
        allProducts.sort((p1, p2) -> Long.compare(p1.getProductId(), p2.getProductId()));
        
        log.debug("Fetched {} products with complete data", allProducts.size());
        return allProducts;
    }
    
    /**
     * Get product by ID with type-specific data
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        log.debug("Fetching product by ID with type-specific data: {}", id);
        
        // Try to find in specific repositories first to get complete data
        Optional<Product> product = bookRepository.findById(id).map(Product.class::cast);
        if (product.isPresent()) return product;
        
        product = cdRepository.findById(id).map(Product.class::cast);
        if (product.isPresent()) return product;
        
        product = dvdRepository.findById(id).map(Product.class::cast);
        if (product.isPresent()) return product;
        
        product = lpRepository.findById(id).map(Product.class::cast);
        if (product.isPresent()) return product;
        
        // Fallback to base repository if not found in specific repositories
        return productRepository.findById(id);
    }
    
    /**
     * Get product by barcode
     */
    @Transactional(readOnly = true)
    public Optional<Product> getProductByBarcode(String barcode) {
        log.debug("Fetching product by barcode: {}", barcode);
        return productRepository.findByBarcode(barcode);
    }
    
    /**
     * Get products by type
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsByType(String type) {
        log.debug("Fetching products by type: {}", type);
        return productRepository.findByType(type);
    }
    
    /**
     * Search products by criteria
     */
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String title, String type, Integer minPrice, 
                                      Integer maxPrice, Boolean inStock) {
        log.debug("Searching products with criteria - title: {}, type: {}, minPrice: {}, maxPrice: {}, inStock: {}", 
                 title, type, minPrice, maxPrice, inStock);
        return productRepository.searchProducts(title, type, minPrice, maxPrice, inStock);
    }
    
    /**
     * Save product (handles polymorphism)
     */
    public Product saveProduct(Product product) {
        log.info("Saving product: {}", product.getTitle());
        
        // Validate product
        validateProduct(product);
        
        // Find appropriate handler and save
        ProductTypeHandler handler = findHandlerForProduct(product);
        Product savedProduct = handler.save(product);
        
        // Log the action
        auditLogService.logAction(
            "Product Saved",
            "Product",
            savedProduct.getProductId(),
            AuditLog.ActionType.CREATE,
            null
        );
        
        log.info("Product saved successfully with ID: {}", savedProduct.getProductId());
        return savedProduct;
    }
    
    /**
     * Update product
     */
    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        
        // Update common fields
        existingProduct.setTitle(updatedProduct.getTitle());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setWeight(updatedProduct.getWeight());
        existingProduct.setRushOrderSupported(updatedProduct.getRushOrderSupported());
        existingProduct.setImageUrl(updatedProduct.getImageUrl());
        existingProduct.setBarcode(updatedProduct.getBarcode());
        existingProduct.setImportDate(updatedProduct.getImportDate());
        existingProduct.setIntroduction(updatedProduct.getIntroduction());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        
        // Update type-specific fields and save
        ProductTypeHandler handler = findHandlerForProduct(existingProduct);
        handler.updateTypeSpecificFields(existingProduct, updatedProduct);
        Product savedProduct = handler.save(existingProduct);
        
        // Log the action
        auditLogService.logAction(
            "Product Updated",
            "Product",
            savedProduct.getProductId(),
            AuditLog.ActionType.UPDATE,
            null
        );
        
        log.info("Product updated successfully: {}", savedProduct.getProductId());
        return savedProduct;
    }
    
    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        
        productRepository.delete(product);
        
        // Log the action
        auditLogService.logAction(
            "Product Deleted",
            "Product",
            id,
            AuditLog.ActionType.DELETE,
            null
        );
        
        log.info("Product deleted successfully: {}", id);
    }
    
    /**
     * Update product stock
     */
    public Product updateStock(Long productId, int quantity) {
        log.info("Updating stock for product {} by {}", productId, quantity);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        if (quantity > 0) {
            product.addStock(quantity);
        } else {
            product.reduceStock(Math.abs(quantity));
        }
        
        Product savedProduct = productRepository.save(product);
        
        auditLogService.logAction(
            "Stock Updated",
            "Product",
            productId,
            AuditLog.ActionType.UPDATE,
            null
        );
        
        log.info("Stock updated for product {}: new quantity = {}", productId, savedProduct.getQuantity());
        return savedProduct;
    }
    
    /**
     * Check product availability
     */
    @Transactional(readOnly = true)
    public boolean checkAvailability(Long productId, int requestedQuantity) {
        log.debug("Checking availability for product {} with quantity {}", productId, requestedQuantity);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
        
        return product.hasStock(requestedQuantity);
    }
    
    /**
     * Get low stock products
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        log.debug("Fetching products with stock below: {}", threshold);
        return productRepository.findLowStockProducts(threshold);
    }
    
    /**
     * Get products with rush order support
     */
    @Transactional(readOnly = true)
    public List<Product> getRushOrderProducts() {
        log.debug("Fetching products with rush order support");
        return productRepository.findByRushOrderSupportedTrue();
    }
    
    /**
     * Validate product data
     */
    private void validateProduct(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Product title is required");
        }
        
        if (product.getPrice() == null || product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        
        if (product.getBarcode() != null && !product.getBarcode().trim().isEmpty()) {
            Optional<Product> existingProduct = productRepository.findByBarcode(product.getBarcode());
            if (existingProduct.isPresent() && !existingProduct.get().getProductId().equals(product.getProductId())) {
                throw new IllegalArgumentException("Product with barcode " + product.getBarcode() + " already exists");
            }
        }
        
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }
    }
    
    /**
     * Get product count by type
     */
    @Transactional(readOnly = true)
    public long getProductCountByType(String type) {
        return productRepository.countByType(type);
    }
    
    /**
     * Get all books
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    /**
     * Get all CDs
     */
    @Transactional(readOnly = true)
    public List<CD> getAllCDs() {
        return cdRepository.findAll();
    }
    
    /**
     * Get all DVDs
     */
    @Transactional(readOnly = true)
    public List<DVD> getAllDVDs() {
        return dvdRepository.findAll();
    }
    
    /**
     * Get all LPs
     */
    @Transactional(readOnly = true)
    public List<LP> getAllLPs() {
        return lpRepository.findAll();
    }
    
    /**
     * Find the appropriate handler for a product
     */
    private ProductTypeHandler findHandlerForProduct(Product product) {
        return productTypeHandlers.stream()
            .filter(handler -> handler.canHandle(product))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "No handler found for product type: " + product.getClass().getSimpleName()));
    }
}