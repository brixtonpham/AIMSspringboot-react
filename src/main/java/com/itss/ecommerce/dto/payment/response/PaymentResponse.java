package com.itss.ecommerce.dto.payment.response;

public class PaymentResponse {
    private String code;
    private String message;
    private String paymentUrl;
    private String ipAddress;

    private PaymentResponse(Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.paymentUrl = builder.paymentUrl;
        this.ipAddress = builder.ipAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public static class Builder {
        private String code;
        private String message;
        private String paymentUrl;
        private String ipAddress;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder paymentUrl(String paymentUrl) {
            this.paymentUrl = paymentUrl;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public PaymentResponse build() {
            return new PaymentResponse(this);
        }
    }
}
