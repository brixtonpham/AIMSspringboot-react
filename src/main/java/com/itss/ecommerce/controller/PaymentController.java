package com.itss.ecommerce.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.itss.ecommerce.dto.payment.PaymentReturnResponse;
import com.itss.ecommerce.dto.payment.request.PaymentRequest;
import com.itss.ecommerce.dto.payment.response.PaymentResponse;
import com.itss.ecommerce.entity.Invoice;
import com.itss.ecommerce.entity.PaymentTransaction;
import com.itss.ecommerce.service.InvoiceService;
import com.itss.ecommerce.service.PaymentTransactionService;
import com.itss.ecommerce.service.admin.OrderService;
import com.itss.ecommerce.service.notification.INotificationService;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;
import com.itss.ecommerce.service.payment.PaymentServiceFactory;
import com.itss.ecommerce.service.payment.gateway.IPaymentService;
import com.itss.ecommerce.service.payment.type.PaymentMethod;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/payment")
@Slf4j
@RequiredArgsConstructor
@Controller
public class PaymentController {

    private final PaymentServiceFactory paymentServiceFactory;
    private final OrderService orderService;
    private final PaymentTransactionService paymentTransactionService;
    private final InvoiceService invoiceService;
    private final INotificationService notificationService;

    /**
     * REST API Controllers for Payment Gateway integration
     * Supports multiple payment gateways through PaymentServiceFactory
     */
    /**
     * API endpoint for creating a new payment
     * Generates payment URL with gateway-specific signature
     *
     * @param request        Payment request with amount, payment method, and other
     *                       details
     * @param servletRequest HTTP request for client IP
     * @return ResponseEntity with payment URL and status
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            HttpServletRequest servletRequest) {
        // Get payment service based on requested payment method
        IPaymentService paymentService = paymentServiceFactory.getPaymentService(request.getPaymentMethod());
        PaymentResponse response = paymentService.createPayment(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/return")
    public RedirectView returnPage(
            @RequestParam Map<String, String> requestParams,
            @RequestParam(required = false, defaultValue = "VNPAY") String paymentMethod,
            HttpServletRequest request) {

        // Get the appropriate payment service based on the payment method
        IPaymentService paymentService = paymentServiceFactory.getPaymentService(
                PaymentMethod.valueOf(paymentMethod.toUpperCase()));

        // Remove paymentMethod parameter to avoid contaminating payment gateway validation
        Map<String, String> cleanParams = new HashMap<>(requestParams);
        cleanParams.remove("paymentMethod");

        // Process payment return using the specific payment service
        PaymentReturnResponse paymentReturn = paymentService.processPaymentReturn(cleanParams);

        String status = paymentReturn.isSuccess() ? "success" : "fail";

        if (paymentReturn.isSuccess() && paymentReturn.getTransactionId() != null) {

            Long orderId = Long.parseLong(paymentReturn.getTransactionId());
            orderService.getOrderById(orderId).ifPresent(order -> {
                notificationService.sendPaymentConfirmation(order, NotificationServiceProvider.EMAIL);
            });

            // Log successful payment
            log.info("Payment successful for order ID: {}", paymentReturn.getTransactionId());

            // Update invoice status to paid
            Invoice invoice = invoiceService.updateInvoiceStatus(orderId, Invoice.PaymentStatus.PAID);

            if (invoice != null) {
                // Update payment transaction status to success
                PaymentTransaction paymentTransaction = paymentTransactionService
                        .findPendingPaymentTransactionsByInvoiceId(invoice.getInvoiceId());
                if (paymentTransaction != null) {
                    paymentTransactionService.updatePaymentTransactionStatus(paymentTransaction,
                            PaymentTransaction.TransactionStatus.SUCCESS);
                }
            }

        } else {
            try {
                if (paymentReturn.getTransactionId() != null) {
                    Long orderId = Long.parseLong(paymentReturn.getTransactionId());
                    orderService.cancelOrder(orderId, "Payment failed or cancelled");
                }
            } catch (Exception e) {
                log.error("Error processing failed payment for order ID: {}", paymentReturn.getTransactionId(), e);
            }
        }

        // Build redirect URL with query params for frontend
        String redirectUrl = String.format(
                "http://localhost:5173/order-confirmation?status=%s&orderId=%s&amount=%s&orderInfo=%s&payDate=%s",
                status,
                paymentReturn.getTransactionId() != null ? paymentReturn.getTransactionId() : "",
                paymentReturn.getAmount(),
                paymentReturn.getOrderInfo() != null ? paymentReturn.getOrderInfo() : "",
                paymentReturn.getPaymentDate() != null ? paymentReturn.getPaymentDate().toString() : "");

        return new RedirectView(redirectUrl);
    }

}
