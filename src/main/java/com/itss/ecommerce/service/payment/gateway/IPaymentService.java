package com.itss.ecommerce.service.payment.gateway;


import com.itss.ecommerce.dto.payment.request.PaymentRequest;
import com.itss.ecommerce.dto.payment.request.QueryRequest;
import com.itss.ecommerce.dto.payment.request.RefundRequest;
import com.itss.ecommerce.dto.payment.response.IPNResponse;
import com.itss.ecommerce.dto.payment.response.PaymentResponse;
import com.itss.ecommerce.dto.payment.response.QueryResponse;
import com.itss.ecommerce.dto.payment.response.RefundResponse;
import com.itss.ecommerce.service.payment.type.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IPaymentService {
    
    PaymentResponse createPayment(PaymentRequest request, HttpServletRequest servletRequest);
    
    QueryResponse queryTransaction(QueryRequest request, HttpServletRequest servletRequest);
    
    RefundResponse refundTransaction(RefundRequest request, HttpServletRequest servletRequest);
    
    IPNResponse handleIpnRequest(Map<String, String> params);
    
    String generateSecureHash(Map<String, String> params);
    
    PaymentMethod getPaymentMethod();
}