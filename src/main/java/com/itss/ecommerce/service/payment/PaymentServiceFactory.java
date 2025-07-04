package com.itss.ecommerce.service.payment;

import org.springframework.stereotype.Component;

import com.itss.ecommerce.service.payment.gateway.IPaymentService;
import com.itss.ecommerce.service.payment.type.PaymentMethod;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentServiceFactory {

    private final Map<PaymentMethod, IPaymentService> paymentServices;
    
    public PaymentServiceFactory(List<IPaymentService> paymentServices) { 
        this.paymentServices = paymentServices.stream()
                .collect(Collectors.toMap(
                        IPaymentService::getPaymentMethod,
                        Function.identity()
                ));
    }

    public IPaymentService getPaymentService(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method cannot be null");
        }

        IPaymentService service = paymentServices.values().stream()
                .filter(ps -> ps.getPaymentMethod().equals(paymentMethod))
                .findFirst()
                .orElse(null);

        if (service == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        return service;
    }

    public IPaymentService getDefaultPaymentService() {
        return getPaymentService(PaymentMethod.VNPAY);
    }
}