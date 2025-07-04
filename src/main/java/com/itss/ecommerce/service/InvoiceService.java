package com.itss.ecommerce.service;

import com.itss.ecommerce.entity.AuditLog;
import com.itss.ecommerce.entity.Invoice;
import com.itss.ecommerce.entity.Order;
import com.itss.ecommerce.entity.PaymentTransaction;
import com.itss.ecommerce.repository.InvoiceRepository;
import com.itss.ecommerce.repository.OrderRepository;
import com.itss.ecommerce.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    
    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final AuditLogService auditLogService;
    
    /**
     * Get all invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        log.debug("Fetching all invoices");
        return invoiceRepository.findAllByOrderByCreatedAtDesc();
    }
    
    /**
     * Get invoice by ID
     */
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        log.debug("Fetching invoice by ID: {}", id);
        return invoiceRepository.findById(id);
    }
    
    /**
     * Get invoice by order ID
     */
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByOrderId(Long orderId) {
        log.debug("Fetching invoice by order ID: {}", orderId);
        return invoiceRepository.findByOrderOrderId(orderId);
    }
    
    /**
     * Create invoice for order
     */
    public Invoice createInvoice(Long orderId, String description) {
        log.info("Creating invoice for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        // Check if invoice already exists
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrderOrderId(orderId);
        if (existingInvoice.isPresent()) {
            throw new IllegalStateException("Invoice already exists for order: " + orderId);
        }
        
        Invoice invoice = new Invoice();
        invoice.createInvoice(order, description);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Log the action
        auditLogService.logAction(
            "Invoice Created",
            "Invoice",
            savedInvoice.getInvoiceId(),
            AuditLog.ActionType.CREATE,
            null
        );
        
        log.info("Invoice created successfully with ID: {}", savedInvoice.getInvoiceId());
        return savedInvoice;
    }
    
    /**
     * Mark invoice as paid
     */
    public Invoice markAsPaid(Long invoiceId, String transactionId, String paymentMethod) {
        log.info("Marking invoice {} as paid with transaction: {}", invoiceId, transactionId);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        if (invoice.isPaid()) {
            throw new IllegalStateException("Invoice is already paid");
        }
        
        
        invoice.markAsPaid(paymentTransactionRepository.findByTransactionId(Long.parseLong(transactionId)));
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Log the payment
        auditLogService.logPayment(
            invoice.getOrder().getOrderId(),
            null,
            invoice.getTotalAmount().toString(),
            "PAID"
        );
        
        log.info("Invoice {} marked as paid successfully", invoiceId);
        return savedInvoice;
    }
    
    /**
     * Mark invoice as failed
     */
    public Invoice markAsFailed(Long invoiceId, String reason) {
        log.info("Marking invoice {} as failed: {}", invoiceId, reason);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        invoice.markAsFailed();
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Log the payment failure
        auditLogService.logPayment(
            invoice.getOrder().getOrderId(),
            null,
            invoice.getTotalAmount().toString(),
            "FAILED: " + reason
        );
        
        log.info("Invoice {} marked as failed", invoiceId);
        return savedInvoice;
    }
    
    /**
     * Get invoices by payment status
     */
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByStatus(Invoice.PaymentStatus status) {
        log.debug("Fetching invoices by status: {}", status);
        return invoiceRepository.findByPaymentStatus(status);
    }
    
    /**
     * Get paid invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getPaidInvoices() {
        log.debug("Fetching paid invoices");
        return invoiceRepository.findPaidInvoices();
    }
    
    /**
     * Get pending invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getPendingInvoices() {
        log.debug("Fetching pending invoices");
        return invoiceRepository.findPendingInvoices();
    }
    
    /**
     * Get failed invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getFailedInvoices() {
        log.debug("Fetching failed invoices");
        return invoiceRepository.findFailedInvoices();
    }
    
    /**
     * Get invoice by transaction ID
     */
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByTransactionId(Long transactionId) {
        log.debug("Fetching invoice by transaction ID: {}", transactionId);
        return Optional.ofNullable(paymentTransactionRepository.findInvoiceByTransactionId(transactionId));
    }
    
    /**
     * Get total paid revenue
     */
    @Transactional(readOnly = true)
    public Long getTotalPaidRevenue() {
        log.debug("Calculating total paid revenue");
        Long revenue = invoiceRepository.getTotalPaidRevenue();
        return revenue != null ? revenue : 0L;
    }
    
    /**
     * Get revenue for date range
     */
    @Transactional(readOnly = true)
    public Long getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating revenue between {} and {}", startDate, endDate);
        Long revenue = invoiceRepository.getTotalRevenueByDateRange(startDate, endDate);
        return revenue != null ? revenue : 0L;
    }
    
    /**
     * Get overdue invoices
     */
    @Transactional(readOnly = true)
    public List<Invoice> getOverdueInvoices(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        log.debug("Fetching invoices overdue by {} days", days);
        return invoiceRepository.findOverdueInvoices(cutoff);
    }
    
    /**
     * Get payment method statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentMethodStatistics() {
        log.debug("Fetching payment method statistics");
        return invoiceRepository.getPaymentMethodStatistics();
    }
    
    /**
     * Get daily revenue statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getDailyRevenueStatistics(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        log.debug("Fetching daily revenue statistics for last {} days", days);
        return invoiceRepository.getDailyRevenueStatistics(startDate);
    }
    
    /**
     * Count invoices by status
     */
    @Transactional(readOnly = true)
    public long countInvoicesByStatus(Invoice.PaymentStatus status) {
        return invoiceRepository.countByPaymentStatus(status);
    }
    
    /**
     * Process payment (integration point with payment gateways)
     */
    public Invoice processPayment(Long invoiceId, String paymentMethod, String transactionData) {
        log.info("Processing payment for invoice: {} via {}", invoiceId, paymentMethod);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        if (invoice.isPaid()) {
            throw new IllegalStateException("Invoice is already paid");
        }
        
        try {
            // Here you would integrate with actual payment gateways
            // For demo purposes, we'll simulate success
            
            // String transactionId = generateTransactionId();
            //invoice.markAsPaid(paymentTransactionRepository.save(
            //    new PaymentTransaction(transactionId, invoice, paymentMethod, transactionData)
            //));
            
            Invoice savedInvoice = invoiceRepository.save(invoice);
            
            // Log successful payment
            auditLogService.logPayment(
                invoice.getOrder().getOrderId(),
                null,
                invoice.getTotalAmount().toString(),
                "PROCESSED via " + paymentMethod
            );
            
            log.info("Payment processed successfully for invoice: {}", invoiceId);
            return savedInvoice;
            
        } catch (Exception e) {
            log.error("Payment processing failed for invoice: {}", invoiceId, e);
            
            invoice.markAsFailed();
            invoiceRepository.save(invoice);
            
            // Log payment failure
            auditLogService.logPayment(
                invoice.getOrder().getOrderId(),
                null,
                invoice.getTotalAmount().toString(),
                "FAILED: " + e.getMessage()
            );
            
            throw new RuntimeException("Payment processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
    
    /**
     * Refund payment
     */
    public Invoice refundPayment(Long invoiceId, String reason) {
        log.info("Processing refund for invoice: {} - Reason: {}", invoiceId, reason);
        
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
        
        if (!invoice.isPaid()) {
            throw new IllegalStateException("Cannot refund unpaid invoice");
        }
        
        invoice.setPaymentStatus(Invoice.PaymentStatus.REFUNDED);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        // Log the refund
        auditLogService.logPayment(
            invoice.getOrder().getOrderId(),
            null,
            invoice.getTotalAmount().toString(),
            "REFUNDED: " + reason
        );
        
        log.info("Refund processed for invoice: {}", invoiceId);
        return savedInvoice;
    }
    public void saveInvoice(Invoice invoice) {
        log.debug("Saving invoice: {}", invoice);
        invoiceRepository.save(invoice);
    }
}