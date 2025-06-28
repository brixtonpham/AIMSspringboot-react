package com.itss.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.Invoice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    /**
     * Find invoice by order ID
     */
    Optional<Invoice> findByOrderOrderId(Long orderId);
    
    /**
     * Find invoices by payment status
     */
    List<Invoice> findByPaymentStatus(Invoice.PaymentStatus paymentStatus);
    
    /**
     * Find paid invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    List<Invoice> findPaidInvoices();
    
    /**
     * Find pending invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = 'PENDING'")
    List<Invoice> findPendingInvoices();
    
    /**
     * Find failed payment invoices
     */
    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = 'FAILED'")
    List<Invoice> findFailedInvoices();
    
    /**
     * Find invoices by payment method
     */
    List<Invoice> findByPaymentMethod(String paymentMethod);
    
    /**
     * Find invoices created within date range
     */
    @Query("SELECT i FROM Invoice i WHERE i.createdAt BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find invoices paid within date range
     */
    @Query("SELECT i FROM Invoice i WHERE i.paidAt BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesPaidInDateRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get total revenue from paid invoices
     */
    @Query("SELECT SUM(i.order.totalAfterVat) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    Long getTotalPaidRevenue();
    
    /**
     * Get total revenue for date range (paid invoices only)
     */
    @Query("SELECT SUM(i.order.totalAfterVat) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paidAt BETWEEN :startDate AND :endDate")
    Long getTotalRevenueByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count invoices by payment status
     */
    long countByPaymentStatus(Invoice.PaymentStatus paymentStatus);
    
    /**
     * Find overdue invoices (pending for more than specified days)
     */
    @Query("SELECT i FROM Invoice i WHERE i.paymentStatus = 'PENDING' AND i.createdAt < :cutoffDate")
    List<Invoice> findOverdueInvoices(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Get payment method statistics
     */
    @Query("SELECT i.paymentMethod, COUNT(i), SUM(i.order.totalAfterVat) FROM Invoice i WHERE i.paymentStatus = 'PAID' GROUP BY i.paymentMethod")
    List<Object[]> getPaymentMethodStatistics();
    
    /**
     * Find invoices ordered by creation date (newest first)
     */
    List<Invoice> findAllByOrderByCreatedAtDesc();
    
    /**
     * Get daily revenue statistics
     */
    @Query("SELECT DATE(i.paidAt), COUNT(i), SUM(i.order.totalAfterVat) FROM Invoice i WHERE i.paymentStatus = 'PAID' AND i.paidAt >= :startDate GROUP BY DATE(i.paidAt) ORDER BY DATE(i.paidAt)")
    List<Object[]> getDailyRevenueStatistics(@Param("startDate") LocalDateTime startDate);
}