package com.itss.ecommerce.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

import com.itss.ecommerce.dto.payment.request.PaymentRequest;
import com.itss.ecommerce.dto.payment.response.PaymentResponse;
import com.itss.ecommerce.entity.Invoice;
import com.itss.ecommerce.entity.PaymentTransaction;
import com.itss.ecommerce.service.EmailService;
import com.itss.ecommerce.service.InvoiceService;
import com.itss.ecommerce.service.OrderService;
import com.itss.ecommerce.service.PaymentTransactionService;
import com.itss.ecommerce.service.notification.INotificationService;
import com.itss.ecommerce.service.notification.type.NotificationServiceProvider;
import com.itss.ecommerce.service.payment.PaymentServiceFactory;
import com.itss.ecommerce.service.payment.gateway.IPaymentService;

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
    private final EmailService emailService;
    private final OrderService orderService;
    private final PaymentTransactionService paymentTransactionService;
    private final InvoiceService invoiceService;
    private final INotificationService notificationService;
    /**
     * REST API Controllers for VNPAY integration
     */
    /**
     * API endpoint for creating a new payment
     * Generates payment URL with VNPAY signature
     *
     * @param request        Payment request with amount and other details
     * @param servletRequest HTTP request for client IP
     * @return ResponseEntity with payment URL and status
     */
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            HttpServletRequest servletRequest) {
        IPaymentService paymentService = paymentServiceFactory.getDefaultPaymentService();
        PaymentResponse response = paymentService.createPayment(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/return")
    public RedirectView returnPage(
            @RequestParam Map<String, String> requestParams,
            HttpServletRequest request) {

        // Prepare fields for hash validation
        Map<String, String> fields = new HashMap<>(requestParams);
        String vnp_SecureHash = fields.get("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Validate hash
        boolean validHash = paymentServiceFactory.getDefaultPaymentService().generateSecureHash(fields)
                .equals(vnp_SecureHash);

        // Extract needed info
        String txnRef = fields.get("vnp_TxnRef");
        String responseCode = fields.get("vnp_ResponseCode");
        String amount = fields.getOrDefault("vnp_Amount", "0");
        String orderInfo = fields.get("vnp_OrderInfo");
        String payDate = fields.get("vnp_PayDate");

        String status = "fail";
        if (validHash && "00".equals(responseCode) && txnRef != null) {
            status = "success";
            try {
                Long orderId = Long.parseLong(txnRef);
                orderService.getOrderById(orderId).ifPresent(order -> {
                    notificationService.sendPaymentConfirmation(order, NotificationServiceProvider.EMAIL);
                    System.out.println("Payment confirmation sent to email: " + order.getDeliveryInformation().getEmail());
                });

                // Log successful payment
                log.info("Payment successful for order ID: {}", txnRef);

                // Update invoice status to paid
                Optional<Invoice> invoice = invoiceService.getInvoiceByOrderId(Long.parseLong(txnRef));
                if (invoice.isPresent()) {
                    Invoice inv = invoice.get();
                    inv.setPaymentStatus(Invoice.PaymentStatus.PAID);
                    inv.setPaidAt(LocalDateTime.now());
                    invoiceService.saveInvoice(inv);

                    // Update payment transaction status
                    PaymentTransaction paymentTransaction = paymentTransactionService
                            .findPendingPaymentTransactionsByInvoiceId(inv.getInvoiceId());
                    if (paymentTransaction != null) {
                        paymentTransactionService.updatePaymentTransactionStatus(paymentTransaction,
                                PaymentTransaction.TransactionStatus.SUCCESS);
                    }
                } else {
                    // Handle case where invoice is not found
                }
            } catch (Exception e) {
                // Log error if needed
            }
        } else {
            try {
                Long orderId = Long.parseLong(txnRef);
                orderService.deleteOrderById(orderId);
            } catch (Exception e) {
                // Log error if needed
            }
        }

        // Build redirect URL with query params for frontend
        String redirectUrl = String.format(
                "http://localhost:5173/order-confirmation?status=%s&orderId=%s&amount=%s&orderInfo=%s&payDate=%s",
                status,
                txnRef != null ? txnRef : "",
                amount,
                orderInfo != null ? orderInfo : "",
                payDate != null ? payDate : "");

        return new RedirectView(redirectUrl);
    }

}
