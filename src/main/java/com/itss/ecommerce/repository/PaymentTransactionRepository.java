package com.itss.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itss.ecommerce.entity.Invoice;
import com.itss.ecommerce.entity.PaymentTransaction;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {

    /**
     * Find transactions by invoice ID
     */
    List<PaymentTransaction> findByInvoiceInvoiceId(Long invoiceId);

    /**
     * Find successful transactions by invoice ID
     */
    List<PaymentTransaction> findByInvoiceInvoiceIdAndStatus(Long invoiceId, PaymentTransaction.TransactionStatus status);

    /**
     * Find transactions by status
     */
    List<PaymentTransaction> findByStatus(PaymentTransaction.TransactionStatus status);

    /**
     * Find transactions by payment method
     */
    List<PaymentTransaction> findByPaymentMethod(String paymentMethod); 

    /**
     * Find transaction by transaction ID   
     */
    PaymentTransaction findByTransactionId(Long transactionId);

    /**
     * Find invoice by transaction ID
     */
    Invoice findInvoiceByTransactionId(Long transactionId);

    PaymentTransaction findSuccessByInvoice(Invoice invoice);
}
