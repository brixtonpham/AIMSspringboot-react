package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.Book;
import com.itss.ecommerce.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Book sampleBook1;
    private Book sampleBook2;
    private Book lowStockBook;

    @BeforeEach
    void setUp() {
        // Sample Book 1
        sampleBook1 = new Book();
        sampleBook1.setTitle("Java Programming Guide");
        sampleBook1.setPrice(250000);
        sampleBook1.setQuantity(15);
        sampleBook1.setWeight(0.8f);
        sampleBook1.setRushOrderSupported(true);
        sampleBook1.setAuthors("John Smith");
        sampleBook1.setGenre("Programming");
        sampleBook1.setPublishers("Tech Books Publishing");
        sampleBook1.setPageCount(500);
        sampleBook1.setImportDate("2024-01-15");

        // Sample Book 2
        sampleBook2 = new Book();
        sampleBook2.setTitle("Spring Boot in Action");
        sampleBook2.setPrice(300000);
        sampleBook2.setQuantity(8);
        sampleBook2.setWeight(0.9f);
        sampleBook2.setRushOrderSupported(false);
        sampleBook2.setAuthors("Jane Doe");
        sampleBook2.setGenre("Programming");
        sampleBook2.setPublishers("Spring Press");
        sampleBook2.setPageCount(600);
        sampleBook2.setImportDate("2024-01-20");

        // Low Stock Book
        lowStockBook = new Book();
        lowStockBook.setTitle("Rare Programming Book");
        lowStockBook.setPrice(500000);
        lowStockBook.setQuantity(2); // Low stock
        lowStockBook.setWeight(1.2f);
        lowStockBook.setRushOrderSupported(true);
        lowStockBook.setAuthors("Expert Author");
        lowStockBook.setGenre("Advanced Programming");
        lowStockBook.setPublishers("Rare Books Ltd");
        lowStockBook.setPageCount(800);
        lowStockBook.setImportDate("2024-01-25");

        // Persist test data
        entityManager.persistAndFlush(sampleBook1);
        entityManager.persistAndFlush(sampleBook2);
        entityManager.persistAndFlush(lowStockBook);
    }

    @Test
    @DisplayName("Test Find All Products")
    void testFindAllProducts() {
        // When
        List<Product> products = productRepository.findAll();

        // Then
        assertThat(products)
            .hasSize(3)
            .extracting(Product::getTitle)
            .containsExactlyInAnyOrder(
                "Java Programming Guide",
                "Spring Boot in Action", 
                "Rare Programming Book"
            );
    }

    @Test
    @DisplayName("Test Find Product By ID")
    void testFindProductById() {
        // When
        Optional<Product> foundProduct = productRepository.findById(sampleBook1.getProductId());

        // Then
        assertThat(foundProduct)
            .isPresent()
            .get()
            .satisfies(product -> {
                assertThat(product.getTitle()).isEqualTo("Java Programming Guide");
                assertThat(product.getPrice()).isEqualTo(250000);
                assertThat(product.getQuantity()).isEqualTo(15);
            });
    }

    @Test
    @DisplayName("Test Find Product By Non-Existent ID")
    void testFindProductByNonExistentId() {
        // When
        Optional<Product> foundProduct = productRepository.findById(999L);

        // Then
        assertThat(foundProduct).isEmpty();
    }

    @Test
    @DisplayName("Test Find Products By Title Containing")
    void testFindProductsByTitleContaining() {
        // When
        List<Product> programmingBooks = productRepository.findByTitleContainingIgnoreCase("programming");
        List<Product> springBooks = productRepository.findByTitleContainingIgnoreCase("spring");

        // Then
        assertThat(programmingBooks)
            .hasSize(2)
            .extracting(Product::getTitle)
            .containsExactlyInAnyOrder("Java Programming Guide", "Rare Programming Book");

        assertThat(springBooks)
            .hasSize(1)
            .extracting(Product::getTitle)
            .containsExactly("Spring Boot in Action");
    }

    @Test
    @DisplayName("Test Find Products With Rush Order Support")
    void testFindProductsWithRushOrderSupport() {
        // When
        List<Product> rushOrderProducts = productRepository.findByRushOrderSupportedTrue();

        // Then
        assertThat(rushOrderProducts)
            .hasSize(2)
            .extracting(Product::getTitle)
            .containsExactlyInAnyOrder("Java Programming Guide", "Rare Programming Book");

        assertThat(rushOrderProducts)
            .allMatch(Product::getRushOrderSupported);
    }

    @Test
    @DisplayName("Test Find Low Stock Products")
    void testFindLowStockProducts() {
        // When
        List<Product> lowStockProducts = productRepository.findLowStockProducts(5);

        // Then
        assertThat(lowStockProducts)
            .hasSize(1)
            .extracting(Product::getTitle)
            .containsExactly("Rare Programming Book");

        assertThat(lowStockProducts.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test Find Products By Price Range")
    void testFindProductsByPriceRange() {
        // When
        List<Product> expensiveProducts = productRepository.findByPriceGreaterThanEqual(300000);
        List<Product> affordableProducts = productRepository.findByPriceLessThanEqual(300000);

        // Then
        assertThat(expensiveProducts)
            .hasSize(2)
            .extracting(Product::getTitle)
            .containsExactlyInAnyOrder("Spring Boot in Action", "Rare Programming Book");

        assertThat(affordableProducts)
            .hasSize(2)
            .extracting(Product::getTitle)
            .containsExactlyInAnyOrder("Java Programming Guide", "Spring Boot in Action");
    }

    @Test
    @DisplayName("Test Save New Product")
    void testSaveNewProduct() {
        // Given
        Book newBook = new Book();
        newBook.setTitle("New Programming Book");
        newBook.setPrice(200000);
        newBook.setQuantity(10);
        newBook.setAuthors("New Author");
        newBook.setGenre("Programming");
        newBook.setPublishers("New Publisher");

        // When
        Product savedProduct = productRepository.save(newBook);

        // Then
        assertThat(savedProduct.getProductId()).isNotNull();
        assertThat(savedProduct.getTitle()).isEqualTo("New Programming Book");

        // Verify it's persisted
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getProductId());
        assertThat(foundProduct).isPresent();
    }

    @Test
    @DisplayName("Test Update Product")
    void testUpdateProduct() {
        // Given
        Product productToUpdate = productRepository.findById(sampleBook1.getProductId()).orElseThrow();
        String newTitle = "Updated Java Programming Guide";
        Integer newPrice = 280000;

        // When
        productToUpdate.setTitle(newTitle);
        productToUpdate.setPrice(newPrice);
        Product updatedProduct = productRepository.save(productToUpdate);

        // Then
        assertThat(updatedProduct.getTitle()).isEqualTo(newTitle);
        assertThat(updatedProduct.getPrice()).isEqualTo(newPrice);

        // Verify changes are persisted
        entityManager.clear(); // Clear persistence context
        Product refreshedProduct = productRepository.findById(sampleBook1.getProductId()).orElseThrow();
        assertThat(refreshedProduct.getTitle()).isEqualTo(newTitle);
        assertThat(refreshedProduct.getPrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("Test Delete Product")
    void testDeleteProduct() {
        // Given
        Long productIdToDelete = sampleBook1.getProductId();

        // When
        productRepository.deleteById(productIdToDelete);

        // Then
        Optional<Product> deletedProduct = productRepository.findById(productIdToDelete);
        assertThat(deletedProduct).isEmpty();

        // Verify only 2 products remain
        List<Product> remainingProducts = productRepository.findAll();
        assertThat(remainingProducts).hasSize(2);
    }

    @Test
    @DisplayName("Test Find Products Ordered By Price")
    void testFindProductsOrderedByPrice() {
        // When
        List<Product> productsByPriceAsc = productRepository.findAllByOrderByPriceAsc();
        List<Product> productsByPriceDesc = productRepository.findAllByOrderByPriceDesc();

        // Then
        assertThat(productsByPriceAsc)
            .hasSize(3)
            .extracting(Product::getPrice)
            .containsExactly(250000, 300000, 500000);

        assertThat(productsByPriceDesc)
            .hasSize(3)
            .extracting(Product::getPrice)
            .containsExactly(500000, 300000, 250000);
    }

    @Test
    @DisplayName("Test Count Products")
    void testCountProducts() {
        // When
        long totalProducts = productRepository.count();
        long rushOrderProducts = productRepository.countByRushOrderSupportedTrue();

        // Then
        assertThat(totalProducts).isEqualTo(3);
        assertThat(rushOrderProducts).isEqualTo(2);
    }

    @Test
    @DisplayName("Test Product Exists By Title")
    void testProductExistsByTitle() {
        // When
        boolean existsJavaBook = productRepository.existsByTitleIgnoreCase("java programming guide");
        boolean existsNonExistentBook = productRepository.existsByTitleIgnoreCase("non existent book");

        // Then
        assertThat(existsJavaBook).isTrue();
        assertThat(existsNonExistentBook).isFalse();
    }
}
