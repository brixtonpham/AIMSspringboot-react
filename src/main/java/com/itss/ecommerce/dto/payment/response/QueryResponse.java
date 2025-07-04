package com.itss.ecommerce.dto.payment.response;

public class QueryResponse {
    private String vnp_ResponseId;
    private String vnp_Command;
    private String vnp_ResponseCode;
    private String vnp_Message;
    private String vnp_IpAddr;
    private String vnp_TxnRef;
    private String vnp_Amount;
    private String vnp_TransactionNo;
    private String vnp_BankCode;
    private String vnp_PayDate;
    private String vnp_TransactionType;
    private String vnp_TransactionStatus;

    // Getters and setters
    public String getVnp_ResponseId() {
        return vnp_ResponseId;
    }

    public void setVnp_ResponseId(String vnp_ResponseId) {
        this.vnp_ResponseId = vnp_ResponseId;
    }

    public String getVnp_IpAddr() {
        return vnp_IpAddr;
    }

    public void setVnp_IpAddr(String vnp_IpAddr) {
        this.vnp_IpAddr = vnp_IpAddr;
    }

    public String getVnp_Command() {
        return vnp_Command;
    }

    public void setVnp_Command(String vnp_Command) {
        this.vnp_Command = vnp_Command;
    }

    public String getVnp_ResponseCode() {
        return vnp_ResponseCode;
    }

    public void setVnp_ResponseCode(String vnp_ResponseCode) {
        this.vnp_ResponseCode = vnp_ResponseCode;
    }

    public String getVnp_Message() {
        return vnp_Message;
    }

    public void setVnp_Message(String vnp_Message) {
        this.vnp_Message = vnp_Message;
    }

    public String getVnp_TxnRef() {
        return vnp_TxnRef;
    }

    public void setVnp_TxnRef(String vnp_TxnRef) {
        this.vnp_TxnRef = vnp_TxnRef;
    }

    public String getVnp_Amount() {
        return vnp_Amount;
    }

    public void setVnp_Amount(String vnp_Amount) {
        this.vnp_Amount = vnp_Amount;
    }

    public String getVnp_TransactionNo() {
        return vnp_TransactionNo;
    }

    public void setVnp_TransactionNo(String vnp_TransactionNo) {
        this.vnp_TransactionNo = vnp_TransactionNo;
    }

    public String getVnp_BankCode() {
        return vnp_BankCode;
    }

    public void setVnp_BankCode(String vnp_BankCode) {
        this.vnp_BankCode = vnp_BankCode;
    }

    public String getVnp_PayDate() {
        return vnp_PayDate;
    }

    public void setVnp_PayDate(String vnp_PayDate) {
        this.vnp_PayDate = vnp_PayDate;
    }

    public String getVnp_TransactionType() {
        return vnp_TransactionType;
    }

    public void setVnp_TransactionType(String vnp_TransactionType) {
        this.vnp_TransactionType = vnp_TransactionType;
    }

    public String getVnp_TransactionStatus() {
        return vnp_TransactionStatus;
    }

    public void setVnp_TransactionStatus(String vnp_TransactionStatus) {
        this.vnp_TransactionStatus = vnp_TransactionStatus;
    }
}
