package com.itss.ecommerce.controller;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.dto.mapper.ProductMapper;
import com.itss.ecommerce.dto.product.BookDTO;
import com.itss.ecommerce.dto.product.CDDTO;
import com.itss.ecommerce.dto.product.DVDDTO;
import com.itss.ecommerce.dto.product.LPDTO;
import com.itss.ecommerce.entity.*;
import com.itss.ecommerce.service.product.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@Validated
@CrossOrigin(origins = "*")
public class ProductController {
    
    private final ProductService productService;
    /**
     * Get all products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        log.info("GET /api/products - Fetching all products");
        
        List<Product> products = productService.getAllProducts();
        List<ProductDTO> productDTOs = products.stream()
            .map(ProductMapper::mapToSpecificDTO)
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(productDTOs, 
            String.format("Retrieved %d products", productDTOs.size())));
    }
    
    /**
     * Get product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
            @PathVariable @Positive Long id) {
        log.info("GET /api/products/{} - Fetching product", id);
        
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound("Product not found with ID: " + id));
        }
        
        ProductDTO productDTO = ProductMapper.mapToSpecificDTO(product.get());
        return ResponseEntity.ok(ApiResponse.success(productDTO));
    }
    
    /**
     * Get product by barcode
     */
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductByBarcode(
            @PathVariable String barcode) {
        log.info("GET /api/products/barcode/{} - Fetching product", barcode);
        
        Optional<Product> product = productService.getProductByBarcode(barcode);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.notFound("Product not found with barcode: " + barcode));
        }
        
        ProductDTO productDTO = ProductMapper.mapToSpecificDTO(product.get());
        return ResponseEntity.ok(ApiResponse.success(productDTO));
    }
    
    /**
     * Get products by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByType(
            @PathVariable String type) {
        log.info("GET /api/products/type/{} - Fetching products", type);
        
        List<Product> products = productService.getProductsByType(type);
        List<ProductDTO> productDTOs = products.stream()
            .map(ProductMapper::mapToSpecificDTO)
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(productDTOs,
            String.format("Retrieved %d %s products", productDTOs.size(), type)));
    }
    
    /**
     * Search products
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) Boolean inStock) {
        log.info("GET /api/products/search - Searching products with criteria");
        
        List<Product> products = productService.searchProducts(title, type, minPrice, maxPrice, inStock);
        List<ProductDTO> productDTOs = products.stream()
            .map(ProductMapper::mapToSpecificDTO)
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(productDTOs,
            String.format("Found %d products matching criteria", productDTOs.size())));
    }
    
    /**
     * Create new product with type-specific fields
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductFormRequest request) {
        log.info("POST /api/products - Creating new product: {}", request.getProductData().getTitle());
        log.debug("Received request: {}", request);
        log.debug("Product type: {}", request.getProductType());
        log.debug("Book data: {}", request.getBookData());
        log.debug("CD data: {}", request.getCdData());
        log.debug("DVD data: {}", request.getDvdData());
        log.debug("LP data: {}", request.getLpData());
        
        try {
            Product product = ProductMapper.createProductFromRequest(request);
            Product savedProduct = productService.saveProduct(product);
            ProductDTO savedProductDTO = ProductMapper.mapToSpecificDTO(savedProduct);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedProductDTO, "Product created successfully"));
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create product: " + e.getMessage()));
        }
    }
    
    /**
     * Update product with type-specific fields
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable @Positive Long id,
            @Valid @RequestBody ProductFormRequest request) {
        log.info("PUT /api/products/{} - Updating product", id);
        
        try {
            Product updatedProduct = ProductMapper.createProductFromRequest(request);
            updatedProduct.setProductId(id); // Ensure the ID is set for update
            Product savedProduct = productService.updateProduct(id, updatedProduct);
            ProductDTO savedProductDTO = ProductMapper.mapToSpecificDTO(savedProduct);
            
            return ResponseEntity.ok(ApiResponse.success(savedProductDTO, "Product updated successfully"));
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to update product: " + e.getMessage()));
        }
    }
    
    /**
     * Delete product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable @Positive Long id) {
        log.info("DELETE /api/products/{} - Deleting product", id);
        
        productService.deleteProduct(id);
        
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully"));
    }
    
    /**
     * Update product stock
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponse<ProductDTO>> updateStock(
            @PathVariable @Positive Long id,
            @RequestParam int quantity) {
        log.info("PATCH /api/products/{}/stock - Updating stock by {}", id, quantity);
        
        Product updatedProduct = productService.updateStock(id, quantity);
        ProductDTO productDTO = ProductMapper.mapToSpecificDTO(updatedProduct);
        
        return ResponseEntity.ok(ApiResponse.success(productDTO, 
            String.format("Stock updated. New quantity: %d", updatedProduct.getQuantity())));
    }
    
    /**
     * Check product availability
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @PathVariable @Positive Long id,
            @RequestParam @Positive int quantity) {
        log.info("GET /api/products/{}/availability - Checking availability for quantity {}", id, quantity);
        
        boolean available = productService.checkAvailability(id, quantity);
        
        return ResponseEntity.ok(ApiResponse.success(available,
            available ? "Product is available" : "Insufficient stock"));
    }
    
    /**
     * Get low stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") @Positive int threshold) {
        log.info("GET /api/products/low-stock - Fetching products with stock below {}", threshold);
        
        List<Product> products = productService.getLowStockProducts(threshold);
        List<ProductDTO> productDTOs = products.stream()
            .map(ProductMapper::mapToSpecificDTO)
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(productDTOs,
            String.format("Found %d products with low stock", productDTOs.size())));
    }
    
    /**
     * Get rush order products
     */
    @GetMapping("/rush-order")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getRushOrderProducts() {
        log.info("GET /api/products/rush-order - Fetching products with rush order support");
        
        List<Product> products = productService.getRushOrderProducts();
        List<ProductDTO> productDTOs = products.stream()
            .map(ProductMapper::mapToSpecificDTO)
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(productDTOs,
            String.format("Found %d products with rush order support", productDTOs.size())));
    }
    
    /**
     * Get all books
     */
    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<BookDTO>>> getAllBooks() {
        log.info("GET /api/products/books - Fetching all books");
        
        List<Book> books = productService.getAllBooks();
        List<com.itss.ecommerce.dto.product.BookDTO> bookDTOs = ProductMapper.toBookDTOList(books);
        
        return ResponseEntity.ok(ApiResponse.success(bookDTOs,
            String.format("Retrieved %d books", bookDTOs.size())));
    }
    
    /**
     * Get all CDs
     */
    @GetMapping("/cds")
    public ResponseEntity<ApiResponse<List<CDDTO>>> getAllCDs() {
        log.info("GET /api/products/cds - Fetching all CDs");
        
        List<CD> cds = productService.getAllCDs();
        List<CDDTO> cdDTOs = ProductMapper.toCDDTOList(cds);
        
        return ResponseEntity.ok(ApiResponse.success(cdDTOs,
            String.format("Retrieved %d CDs", cdDTOs.size())));
    }
    
    /**
     * Get all DVDs
     */
    @GetMapping("/dvds")
    public ResponseEntity<ApiResponse<List<DVDDTO>>> getAllDVDs() {
        log.info("GET /api/products/dvds - Fetching all DVDs");
        
        List<DVD> dvds = productService.getAllDVDs();
        List<DVDDTO> dvdDTOs = ProductMapper.toDVDDTOList(dvds);
        
        return ResponseEntity.ok(ApiResponse.success(dvdDTOs,
            String.format("Retrieved %d DVDs", dvdDTOs.size())));
    }
    
    /**
     * Create new book
     */
    @PostMapping("/books")
    public ResponseEntity<ApiResponse<BookDTO>> createBook(
            @Valid @RequestBody BookDTO bookDTO) {
        log.info("POST /api/products/books - Creating new book: {}", bookDTO.getTitle());
        
        Book book = ProductMapper.toEntity(bookDTO);
        Product savedProduct = productService.saveProduct(book);
        BookDTO savedBookDTO = ProductMapper.toDTO((Book) savedProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedBookDTO, "Book created successfully"));
    }
    
    /**
     * Create new CD
     */
    @PostMapping("/cds")
    public ResponseEntity<ApiResponse<CDDTO>> createCD(
            @Valid @RequestBody CDDTO cdDTO) {
        log.info("POST /api/products/cds - Creating new CD: {}", cdDTO.getTitle());
        
        CD cd = ProductMapper.toEntity(cdDTO);
        Product savedProduct = productService.saveProduct(cd);
        CDDTO savedCDDTO = ProductMapper.toDTO((CD) savedProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedCDDTO, "CD created successfully"));
    }
    
    /**
     * Create new DVD
     */
    @PostMapping("/dvds")
    public ResponseEntity<ApiResponse<DVDDTO>> createDVD(
            @Valid @RequestBody DVDDTO dvdDTO) {
        log.info("POST /api/products/dvds - Creating new DVD: {}", dvdDTO.getTitle());
        
        DVD dvd = ProductMapper.toEntity(dvdDTO);
        Product savedProduct = productService.saveProduct(dvd);
        DVDDTO savedDVDDTO = ProductMapper.toDTO((DVD) savedProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedDVDDTO, "DVD created successfully"));
    }
    
    /**
     * Get all LPs
     */
    @GetMapping("/lps")
    public ResponseEntity<ApiResponse<List<LPDTO>>> getAllLPs() {
        log.info("GET /api/products/lps - Fetching all LPs");
        
        List<LP> lps = productService.getAllLPs();
        List<LPDTO> lpDTOs = ProductMapper.toLPDTOList(lps);
        
        return ResponseEntity.ok(ApiResponse.success(lpDTOs,
            String.format("Retrieved %d LPs", lpDTOs.size())));
    }
    
    /**
     * Create new LP
     */
    @PostMapping("/lps")
    public ResponseEntity<ApiResponse<LPDTO>> createLP(
            @Valid @RequestBody LPDTO lpDTO) {
        log.info("POST /api/products/lps - Creating new LP: {}", lpDTO.getTitle());
        
        LP lp = ProductMapper.toEntity(lpDTO);
        Product savedProduct = productService.saveProduct(lp);
        LPDTO savedLPDTO = ProductMapper.toDTO((LP) savedProduct);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedLPDTO, "LP created successfully"));
    }
    
}