package com.itss.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.OrderItemList;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemListRepository extends JpaRepository<OrderItemList, Long> {
    
    /**
     * Find orders by status
     */
    List<OrderItemList> findByStatus(OrderItemList.OrderStatus status);
    
    /**
     * Find pending orders
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.status = 'PENDING'")
    List<OrderItemList> findPendingOrders();
    
    /**
     * Find orders created within date range
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<OrderItemList> findOrdersByDateRange(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders by total amount range
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.totalAfterVat BETWEEN :minAmount AND :maxAmount")
    List<OrderItemList> findOrdersByAmountRange(@Param("minAmount") int minAmount, @Param("maxAmount") int maxAmount);
    
    /**
     * Find orders with rush delivery
     */
    @Query("SELECT DISTINCT o FROM OrderItemList o JOIN o.orderItems ol WHERE ol.rushOrder = true")
    List<OrderItemList> findOrdersWithRushDelivery();
    
    /**
     * Find recent orders (last 30 days)
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.createdAt >= :cutoffDate")
    List<OrderItemList> findRecentOrders(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find large orders (total after VAT > specified amount)
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.totalAfterVat > :minAmount")
    List<OrderItemList> findLargeOrders(@Param("minAmount") int minAmount);
    
    /**
     * Count orders by status
     */
    long countByStatus(OrderItemList.OrderStatus status);
    
    /**
     * Get total revenue for date range
     */
    @Query("SELECT SUM(o.totalAfterVat) FROM OrderItemList o WHERE o.status = 'DELIVERED' AND o.createdAt BETWEEN :startDate AND :endDate")
    Long getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find orders by delivery province
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.deliveryInformation.province = :province")
    List<OrderItemList> findOrdersByDeliveryProvince(@Param("province") String province);
    
    /**
     * Find orders that can be cancelled
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.status IN ('PENDING', 'CONFIRMED')")
    List<OrderItemList> findCancellableOrders();
    
    /**
     * Find orders ordered by creation date (newest first)
     */
    List<OrderItemList> findAllByOrderByCreatedAtDesc();
    
    /**
     * Get order statistics
     */
    @Query("SELECT o.status, COUNT(o), SUM(o.totalAfterVat) FROM OrderItemList o GROUP BY o.status")
    List<Object[]> getOrderStatistics();
    
    /**
     * Find orders by customer email (through delivery info)
     */
    @Query("SELECT o FROM OrderItemList o WHERE o.deliveryInformation.email = :email")
    List<OrderItemList> findOrdersByCustomerEmail(@Param("email") String email);
}