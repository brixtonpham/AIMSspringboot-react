/**
 * Controller handling VNPAY payment integration endpoints
 * Provides REST APIs for payment creation, confirmation, query and refund operations
 */
package com.itss.ecommerce.controller.vnpay;

import com.itss.ecommerce.dto.payment.request.PaymentRequest;
import com.itss.ecommerce.dto.payment.request.QueryRequest;
import com.itss.ecommerce.dto.payment.request.RefundRequest;
import com.itss.ecommerce.dto.payment.response.IPNResponse;
import com.itss.ecommerce.entity.Invoice;
import com.itss.ecommerce.entity.PaymentTransaction;
import com.itss.ecommerce.service.EmailService;
import com.itss.ecommerce.service.InvoiceService;
import com.itss.ecommerce.service.OrderService;
import com.itss.ecommerce.dto.payment.response.PaymentResponse;
import com.itss.ecommerce.dto.payment.response.QueryResponse;
import com.itss.ecommerce.dto.payment.response.RefundResponse;
import com.itss.ecommerce.service.payment.PaymentServiceFactory;
import com.itss.ecommerce.service.payment.gateway.IPaymentService;
import com.itss.ecommerce.service.PaymentTransactionService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


//@CrossOrigin(origins = "http://localhost:5173")
//@RequestMapping("/api/payment")
@Slf4j
@RequiredArgsConstructor
@Controller
public class VNPayController {

    // private final VNPayService vnPayService;
    private final EmailService emailService;
    private final OrderService orderService;
    private final PaymentTransactionService paymentTransactionService;
    private final InvoiceService invoiceService;
    private final PaymentServiceFactory paymentServiceFactory;

    /**
     * API endpoint for payment form information
     * 
     * @return payment form info
     */
    @GetMapping("/pay")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> paymentPage() {
        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("endpoint", "/api/payment");
        paymentInfo.put("method", "POST");
        paymentInfo.put("description", "VNPay Payment Integration");
        paymentInfo.put("required_fields", List.of("amount", "bankCode", "language"));
        return ResponseEntity.ok(paymentInfo);
    }

    /**
     * API endpoint for transaction query information
     * 
     * @return query form info
     */
    @GetMapping("/querydr")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> queryPage() {
        Map<String, Object> queryInfo = new HashMap<>();
        queryInfo.put("endpoint", "/api/payment/query");
        queryInfo.put("method", "POST");
        queryInfo.put("description", "VNPay Transaction Query");
        queryInfo.put("required_fields", List.of("orderId", "transDate"));
        return ResponseEntity.ok(queryInfo);
    }

    /**
     * API endpoint for refund request information
     * 
     * @return refund form info
     */
    @GetMapping("/vnpay/refund")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> refundPage() {
        Map<String, Object> refundInfo = new HashMap<>();
        refundInfo.put("endpoint", "/api/payment/refund");
        refundInfo.put("method", "POST");
        refundInfo.put("description", "VNPay Refund Request");
        refundInfo.put("required_fields", List.of("orderId", "amount", "transDate", "user"));
        return ResponseEntity.ok(refundInfo);
    }

    /**
     * Handles the return URL from VNPAY after payment
     * Validates the payment response signature and displays the result
     *
     * @param requestParams Parameters returned from VNPAY
     * @param request       HTTP request
     * @param model         Spring MVC model
     * @return return page view name
     */
    @GetMapping("/vnpay/return")
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
                    emailService.sendOrderConfirmationEmail(order);
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

    /**
     * Renders the IPN test page
     * 
     * @return IPN test view name
     */
    @GetMapping("/ipn")
    public String ipnPage() {
        return "vnpay_ipn";
    }

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
    @PostMapping("/vnpay")
    @ResponseBody
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody PaymentRequest request,
            HttpServletRequest servletRequest) {
        IPaymentService paymentService = paymentServiceFactory.getDefaultPaymentService();
        PaymentResponse response = paymentService.createPayment(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * API endpoint for querying transaction status
     *
     * @param request        Query parameters with order ID
     * @param servletRequest HTTP request for client IP
     * @return ResponseEntity with transaction details
     */
    @PostMapping("/api/payment/query")
    @ResponseBody
    public ResponseEntity<QueryResponse> queryTransaction(
            @RequestBody QueryRequest request,
            HttpServletRequest servletRequest) {
        IPaymentService paymentService = paymentServiceFactory.getDefaultPaymentService();
        QueryResponse response = paymentService.queryTransaction(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * API endpoint for refund requests
     *
     * @param request        Refund details including amount
     * @param servletRequest HTTP request for client IP
     * @return ResponseEntity with refund status
     */
    @PostMapping("/api/payment/refund")
    @ResponseBody
    public ResponseEntity<RefundResponse> refundTransaction(
            @RequestBody RefundRequest request,
            HttpServletRequest servletRequest) {
        IPaymentService paymentService = paymentServiceFactory.getDefaultPaymentService();
        RefundResponse response = paymentService.refundTransaction(request, servletRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Handles IPN (Instant Payment Notification) from VNPAY
     * Validates signature and processes payment confirmation
     *
     * @param requestParams Parameters sent by VNPAY
     * @return ResponseEntity with processing status
     */
    @PostMapping("/ipn")
    @ResponseBody
    public ResponseEntity<IPNResponse> handleIpnNotification(
            @RequestParam MultiValueMap<String, String> requestParams) {

        // Convert MultiValueMap to Map<String, String> for VNPay service
        Map<String, String> vnpParams = new HashMap<>();
        requestParams.forEach((key, value) -> {
            if (value != null && !value.isEmpty()) {
                vnpParams.put(key, value.get(0));
            }
        });
        IPaymentService paymentService = paymentServiceFactory.getDefaultPaymentService();
        IPNResponse response = paymentService.handleIpnRequest(vnpParams);
        return ResponseEntity.ok(response);
    }
}