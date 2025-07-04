package com.itss.ecommerce.service.payment.gateway;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.itss.ecommerce.config.VNPayConfig;
import com.itss.ecommerce.dto.payment.request.PaymentRequest;
import com.itss.ecommerce.dto.payment.request.QueryRequest;
import com.itss.ecommerce.dto.payment.request.RefundRequest;
import com.itss.ecommerce.dto.payment.response.IPNResponse;
import com.itss.ecommerce.dto.payment.response.PaymentResponse;
import com.itss.ecommerce.dto.payment.response.QueryResponse;
import com.itss.ecommerce.dto.payment.response.RefundResponse;
import com.itss.ecommerce.service.payment.type.PaymentMethod;

import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayPaymentService implements IPaymentService {

    private final VNPayConfig vnPayConfig;
    private final RestTemplate restTemplate;

    public VNPayPaymentService(VNPayConfig vnPayConfig) {
        this.vnPayConfig = vnPayConfig;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Creates a VNPAY payment request
     * Builds the payment URL with all required parameters and signature according
     * to VNPAY's specs
     *
     * @param request        Payment request containing amount, bankCode etc.
     * @param servletRequest HTTP request for getting IP address
     * @return PaymentResponse containing the payment URL and status
     */
    public PaymentResponse createPayment(PaymentRequest request, HttpServletRequest servletRequest) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = Long.parseLong(request.getAmount()) * 100;
        String bankCode = request.getBankCode();

        String vnp_TxnRef = request.getOrderId();
        String vnp_IpAddr = vnPayConfig.getIpAddress(servletRequest);
        String vnp_TmnCode = vnPayConfig.getTmnCode();

        LinkedHashMap<String, String> vnp_Params = new LinkedHashMap<>();
        // Add parameters in exact order as example
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        // Optional bank code
        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        // Language setting
        String locate = request.getLanguage();
        vnp_Params.put("vnp_Locale", locate != null && !locate.isEmpty() ? locate : "vn");

        // URLs and IP
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // Timestamps
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Remove any null or empty values before generating hash
        vnp_Params.values().removeIf(value -> value == null || value.trim().isEmpty());

        String query = generatePaymentQuery(vnp_Params);
        String paymentUrl = vnPayConfig.getPayUrl() + "?" + query;

        return PaymentResponse.builder()
                .code("00")
                .message("success")
                .paymentUrl(paymentUrl)
                .ipAddress(vnp_IpAddr)
                .build();
    }

    /**
     * Queries the status of a VNPAY transaction
     *
     * @param request        Query request containing orderId and transaction date
     * @param servletRequest HTTP request for getting IP address
     * @return QueryResponse containing transaction details and status
     */
    public QueryResponse queryTransaction(QueryRequest request, HttpServletRequest servletRequest) {
        String vnp_RequestId = vnPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "querydr";
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        String vnp_TxnRef = request.getOrderId();
        String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
        String vnp_TransDate = request.getTransDate();

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_IpAddr = vnPayConfig.getIpAddress(servletRequest);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_TransactionDate", vnp_TransDate);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        String hashData = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        return callVnpayApi(vnp_Params, QueryResponse.class);
    }

    /**
     * Initiates a refund request for a VNPAY transaction
     *
     * @param request        Refund request with transaction details
     * @param servletRequest HTTP request for getting IP address
     * @return RefundResponse containing the refund status
     */
    public RefundResponse refundTransaction(RefundRequest request, HttpServletRequest servletRequest) {
        String vnp_RequestId = vnPayConfig.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = vnPayConfig.getTmnCode();
        String vnp_TransactionType = request.getTranType();
        String vnp_TxnRef = request.getOrderId();
        long amount = request.getAmount() * 100;
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionDate = request.getTransDate();
        String vnp_CreateBy = request.getUser();

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_IpAddr = vnPayConfig.getIpAddress(servletRequest);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_RequestId", vnp_RequestId);
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_TransactionNo", "");
        vnp_Params.put("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.put("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        String hashData = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, vnp_Amount, "", vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
        String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        return callVnpayApi(vnp_Params, RefundResponse.class);
    }

    /**
     * Handles Instant Payment Notification (IPN) from VNPAY
     * Validates the signature and processes the payment status update
     *
     * @param params Map of parameters received from VNPAY's IPN request
     * @return IPNResponse indicating the processing result
     */
    public IPNResponse handleIpnRequest(Map<String, String> params) {
        try {
            Map<String, String> fields = new HashMap<>(params);
            String vnp_SecureHash = fields.get("vnp_SecureHash");

            if (vnp_SecureHash == null) {
                return new IPNResponse(IPNResponse.INVALID_SIGNATURE, "No signature found");
            }

            // Remove hash fields
            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");

            // Check required fields
            if (!fields.containsKey("vnp_Amount") || !fields.containsKey("vnp_TxnRef") ||
                    !fields.containsKey("vnp_TransactionNo") || !fields.containsKey("vnp_ResponseCode")) {
                return new IPNResponse(IPNResponse.INVALID_SIGNATURE, "Missing required fields");
            }

            // Generate secure hash from remaining fields
            String secureHash = vnPayConfig.hashAllFields(fields);

            // Validate signature
            if (!vnp_SecureHash.equals(secureHash)) {
                return new IPNResponse(IPNResponse.INVALID_SIGNATURE, "Invalid signature");
            }

            // Process payment result
            String vnp_ResponseCode = fields.get("vnp_ResponseCode");

            if ("00".equals(vnp_ResponseCode)) {
                // Payment successful
                // TODO: Update order status
                return new IPNResponse(IPNResponse.SUCCESS, "Confirmed payment success");
            } else {
                // Payment failed
                return new IPNResponse(IPNResponse.ORDER_NOT_FOUND, "Payment failed with code: " + vnp_ResponseCode);
            }

        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return new IPNResponse(IPNResponse.UNKNOWN_ERROR, "Internal server error");
        }
    }

    @Override
    public String generateSecureHash(Map<String, String> params) {
        // Sort parameters by key
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        // Create hash data
        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
            }
        }

        return vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());

    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.VNPAY;
    }

    /**
     * Generates the query string for payment URL
     * Sorts parameters, URL encodes values, and generates signature according to
     * VNPAY specs
     *
     * @param vnp_Params Map of payment parameters
     * @return URL encoded query string with signature
     */
    private String generatePaymentQuery(Map<String, String> vnp_Params) {
        try {
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data - URL encode values
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    // Build query - URL encode both fieldName and value
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = vnPayConfig.hmacSHA512(vnPayConfig.getSecretKey(), hashData.toString());
            return queryUrl + "&vnp_SecureHash=" + vnp_SecureHash;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private <T> T callVnpayApi(Map<String, String> params, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<T> response = restTemplate.postForEntity(
                vnPayConfig.getApiUrl(),
                request,
                responseType);

        return response.getBody();
    }

}