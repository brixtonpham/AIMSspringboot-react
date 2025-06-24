package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Find product by barcode
     */
    Optional<Product> findByBarcode(String barcode);
    
    /**
     * Find products by type (book, cd, dvd)
     */
    List<Product> findByType(String type);
    
    /**
     * Find products by title containing (case insensitive)
     */
    List<Product> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find products with sufficient stock
     */
    @Query("SELECT p FROM Product p WHERE p.quantity >= :minQuantity")
    List<Product> findProductsWithStock(@Param("minQuantity") int minQuantity);
    
    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice);
    
    /**
     * Find products that support rush order
     */
    List<Product> findByRushOrderSupportedTrue();
    
    /**
     * Find products with low stock (less than specified quantity)
     */
    @Query("SELECT p FROM Product p WHERE p.quantity < :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);
    
    /**
     * Count products by type
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.type = :type")
    long countByType(@Param("type") String type);
    
    /**
     * Find products ordered by price
     */
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findAllByOrderByPriceDesc();
    
    /**
     * Search products by multiple criteria
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:inStock IS NULL OR (:inStock = true AND p.quantity > 0) OR (:inStock = false))")
    List<Product> searchProducts(@Param("title") String title,
                               @Param("type") String type,
                               @Param("minPrice") Integer minPrice,
                               @Param("maxPrice") Integer maxPrice,
                               @Param("inStock") Boolean inStock);
}