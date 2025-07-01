package com.itss.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.itss.ecommerce.entity.PaymentTransaction;
import com.itss.ecommerce.repository.PaymentTransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentTransactionService {
    private final PaymentTransactionRepository paymentTransactionRepository;
    
    /**
     * Save payment transaction
     * 
     * @param paymentTransaction the payment transaction to save
     */
    public void savePaymentTransaction(PaymentTransaction paymentTransaction) {
        log.debug("Saving payment transaction: {}", paymentTransaction);
        paymentTransactionRepository.save(paymentTransaction);
    }

    public PaymentTransaction findPendingPaymentTransactionsByInvoiceId(Long invoiceId) {
        log.debug("Finding pending payment transactions for invoice ID: {}", invoiceId);
        List<PaymentTransaction> pendingPaymentTransactions = paymentTransactionRepository.findByInvoiceInvoiceIdAndStatus(invoiceId, PaymentTransaction.TransactionStatus.PENDING);
        for (PaymentTransaction pendingPaymentTransaction : pendingPaymentTransactions) {
            log.debug("Pending transaction found: {}", pendingPaymentTransaction);
            return pendingPaymentTransaction;
        }
        return null;
    }

    public void updatePaymentTransactionStatus(PaymentTransaction paymentTransaction, PaymentTransaction.TransactionStatus status) {
        log.debug("Updating payment transaction status: {} to {}", paymentTransaction.getTransactionId(), status);
        paymentTransaction.setStatus(status);
        paymentTransactionRepository.save(paymentTransaction);
    }

    public void updatePendingPaymentTransactionStatus(Long invoiceId, PaymentTransaction.TransactionStatus status) {
        PaymentTransaction pendingPaymentTransaction = this.findPendingPaymentTransactionsByInvoiceId(invoiceId);
        log.debug("Pending transaction found: {}", pendingPaymentTransaction);
        pendingPaymentTransaction.setStatus(status);
    }   
}
