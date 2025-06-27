/**
 * Controller handling VNPAY payment integration endpoints
 * Provides REST APIs for payment creation, confirmation, query and refund operations
 */
package com.itss.ecommerce.controller.vnpay;

import com.itss.ecommerce.dto.vnpay.IPNResponse;
import com.itss.ecommerce.dto.vnpay.PaymentRequest;
import com.itss.ecommerce.dto.vnpay.QueryRequest;
import com.itss.ecommerce.dto.vnpay.RefundRequest;
import com.itss.ecommerce.service.EmailService;
import com.itss.ecommerce.service.OrderService;
import com.itss.ecommerce.service.VNPayService;
import com.itss.ecommerce.service.VNPayService.PaymentResponse;
import com.itss.ecommerce.service.VNPayService.QueryResponse;
import com.itss.ecommerce.service.VNPayService.RefundResponse;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/payment")
@Controller
public class VNPayController {

    private static final Logger logger = LoggerFactory.getLogger(VNPayController.class);
    private final VNPayService vnPayService;
    private final EmailService emailService;
    private final OrderService orderService;

    public VNPayController(VNPayService vnPayService, EmailService emailService, OrderService orderService) {
        this.vnPayService = vnPayService;
        this.emailService = emailService;
        this.orderService = orderService;
    }

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
    boolean validHash = vnPayService.hashAllFields(fields).equals(vnp_SecureHash);

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
        } catch (Exception e) {
            // Log error if needed
        }
    } else if (validHash && txnRef != null) {
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
        payDate != null ? payDate : ""
    );

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
        PaymentResponse response = vnPayService.createPayment(request, servletRequest);
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
        QueryResponse response = vnPayService.queryTransaction(request, servletRequest);
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
        RefundResponse response = vnPayService.refundTransaction(request, servletRequest);
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

        IPNResponse response = vnPayService.handleIpnRequest(vnpParams);
        return ResponseEntity.ok(response);
    }
}