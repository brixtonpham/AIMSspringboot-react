package com.itss.ecommerce.repository;

import com.itss.ecommerce.entity.OrderItem;
import com.itss.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Find order lines by order ID
     */
    List<OrderItem> findByOrderOrderId(Long orderId);
    
    /**
     * Find order lines by product
     */
    List<OrderItem> findByProduct(Product product);
    
    /**
     * Find order lines by status
     */
    List<OrderItem> findByStatus(OrderItem.OrderItemStatus status);
    
    /**
     * Find rush order lines
     */
    List<OrderItem> findByRushOrderTrue();
    
    /**
     * Find order lines by product type
     */
    @Query("SELECT ol FROM OrderItem ol WHERE ol.product.type = :productType")
    List<OrderItem> findByProductType(@Param("productType") String productType);
    
    /**
     * Find order lines with quantity greater than specified
     */
    List<OrderItem> findByQuantityGreaterThan(int quantity);
    
    /**
     * Get total quantity sold for a product
     */
    @Query("SELECT SUM(ol.quantity) FROM OrderItem ol WHERE ol.product.productId = :productId")
    Long getTotalQuantitySoldForProduct(@Param("productId") Long productId);
    
    /**
     * Get total revenue for a product
     */
    @Query("SELECT SUM(ol.totalFee) FROM OrderItem ol WHERE ol.product.productId = :productId")
    Long getTotalRevenueForProduct(@Param("productId") Long productId);
    
    /**
     * Find best-selling products (by quantity)
     */
    @Query("SELECT ol.product, SUM(ol.quantity) as totalSold FROM OrderItem ol " +
           "GROUP BY ol.product ORDER BY totalSold DESC")
    List<Object[]> findBestSellingProductsByQuantity();
    
    /**
     * Find highest revenue products
     */
    @Query("SELECT ol.product, SUM(ol.totalFee) as totalRevenue FROM OrderItem ol " +
           "GROUP BY ol.product ORDER BY totalRevenue DESC")
    List<Object[]> findHighestRevenueProducts();
    
    /**
     * Find order lines that can be cancelled
     */
    @Query("SELECT ol FROM OrderItem ol WHERE ol.status IN ('PENDING', 'CONFIRMED')")
    List<OrderItem> findCancellableOrderItems();
    
    /**
     * Count order lines by product
     */
    long countByProduct(Product product);
    
    /**
     * Find order lines by order status
     */
    @Query("SELECT ol FROM OrderItem ol WHERE ol.order.status = :orderStatus")
    List<OrderItem> findByOrderStatus(@Param("orderStatus") String orderStatus);
    
    /**
     * Get product sales statistics
     */
    @Query("SELECT ol.product.type, COUNT(ol), SUM(ol.quantity), SUM(ol.totalFee) " +
           "FROM OrderItem ol GROUP BY ol.product.type")
    List<Object[]> getProductSalesStatistics();
}