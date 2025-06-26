<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Payment Result - VNPAY</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet"/>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                height: 100vh;
                color: #2d3748;
                line-height: 1.4;
                overflow: hidden;
            }

            .container {
                max-width: 600px;
                margin: 0 auto;
                padding: 15px;
                height: 100vh;
                display: flex;
                flex-direction: column;
                justify-content: center;
            }

            .payment-card {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(20px);
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
                overflow: hidden;
                animation: slideUp 0.8s ease-out;
                max-height: 85vh;
                display: flex;
                flex-direction: column;
            }

            @keyframes slideUp {
                from {
                    opacity: 0;
                    transform: translateY(40px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .header {
                text-align: center;
                padding: 25px 25px 15px;
                background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
                flex-shrink: 0;
            }

            .header h1 {
                font-size: 24px;
                font-weight: 700;
                color: #1a202c;
                margin-bottom: 6px;
                letter-spacing: -0.5px;
            }

            .header p {
                color: #64748b;
                font-size: 14px;
                font-weight: 400;
            }

            .status-section {
                padding: 25px 25px 20px;
                text-align: center;
                flex-shrink: 0;
            }

            .status-icon {
                width: 60px;
                height: 60px;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 0 auto 16px;
                font-size: 24px;
                animation: bounce 0.6s ease-out 0.3s both;
            }

            @keyframes bounce {
                0%, 20%, 53%, 80%, 100% {
                    animation-timing-function: cubic-bezier(0.215, 0.610, 0.355, 1.000);
                    transform: translate3d(0,0,0) scale(1);
                }
                40%, 43% {
                    animation-timing-function: cubic-bezier(0.755, 0.050, 0.855, 0.060);
                    transform: translate3d(0, -30px, 0) scale(1.1);
                }
                70% {
                    animation-timing-function: cubic-bezier(0.755, 0.050, 0.855, 0.060);
                    transform: translate3d(0, -15px, 0) scale(1.05);
                }
                90% {
                    transform: translate3d(0,-4px,0) scale(1.02);
                }
            }

            .success .status-icon {
                background: linear-gradient(135deg, #10b981, #059669);
                color: white;
                box-shadow: 0 10px 30px rgba(16, 185, 129, 0.3);
            }

            .error .status-icon {
                background: linear-gradient(135deg, #ef4444, #dc2626);
                color: white;
                box-shadow: 0 10px 30px rgba(239, 68, 68, 0.3);
            }

            .invalid .status-icon {
                background: linear-gradient(135deg, #f59e0b, #d97706);
                color: white;
                box-shadow: 0 10px 30px rgba(245, 158, 11, 0.3);
            }

            .status-title {
                font-size: 20px;
                font-weight: 600;
                margin-bottom: 8px;
                animation: fadeIn 0.8s ease-out 0.5s both;
            }

            .success .status-title { color: #059669; }
            .error .status-title { color: #dc2626; }
            .invalid .status-title { color: #d97706; }

            .status-message {
                font-size: 14px;
                color: #64748b;
                animation: fadeIn 0.8s ease-out 0.7s both;
                margin-bottom: 0;
            }

            @keyframes fadeIn {
                from { opacity: 0; transform: translateY(10px); }
                to { opacity: 1; transform: translateY(0); }
            }

            .details-section {
                padding: 0 25px;
                flex: 1;
                overflow-y: auto;
                min-height: 0;
            }

            .details-title {
                font-size: 18px;
                font-weight: 600;
                color: #1a202c;
                margin-bottom: 16px;
                padding-bottom: 8px;
                border-bottom: 2px solid #e2e8f0;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .detail-item {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                padding: 10px 0;
                border-bottom: 1px solid #f1f5f9;
                animation: fadeInUp 0.6s ease-out both;
            }

            .detail-item:last-child {
                border-bottom: none;
            }

            .detail-item:nth-child(1) { animation-delay: 0.1s; }
            .detail-item:nth-child(2) { animation-delay: 0.2s; }
            .detail-item:nth-child(3) { animation-delay: 0.3s; }
            .detail-item:nth-child(4) { animation-delay: 0.4s; }
            .detail-item:nth-child(5) { animation-delay: 0.5s; }
            .detail-item:nth-child(6) { animation-delay: 0.6s; }

            @keyframes fadeInUp {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .detail-label {
                font-weight: 500;
                color: #475569;
                flex: 0 0 140px;
                font-size: 14px;
            }

            .detail-value {
                font-weight: 600;
                color: #1a202c;
                text-align: right;
                flex: 1;
                word-break: break-all;
            }

            .amount-value {
                font-size: 16px;
                color: #059669;
            }

            .actions {
                padding: 20px 25px;
                text-align: center;
                background: #f8fafc;
                border-top: 1px solid #e2e8f0;
                flex-shrink: 0;
            }

            .btn {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 12px 24px;
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                text-decoration: none;
                border-radius: 10px;
                font-weight: 600;
                font-size: 14px;
                transition: all 0.3s ease;
                box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
                animation: fadeIn 1s ease-out 0.8s both;
            }

            .btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
                color: white;
                text-decoration: none;
            }

            .footer {
                text-align: center;
                padding: 12px;
                color: rgba(255, 255, 255, 0.8);
                font-size: 12px;
                animation: fadeIn 1s ease-out 1s both;
            }

            @media (max-width: 768px) {
                .container {
                    padding: 10px;
                }

                .payment-card {
                    max-height: 90vh;
                }

                .header h1 {
                    font-size: 20px;
                }

                .status-icon {
                    width: 50px;
                    height: 50px;
                    font-size: 20px;
                }

                .status-title {
                    font-size: 18px;
                }

                .detail-item {
                    flex-direction: column;
                    gap: 4px;
                    padding: 8px 0;
                }

                .detail-label {
                    flex: none;
                    font-size: 12px;
                }

                .detail-value {
                    text-align: left;
                    font-size: 13px;
                }

                .details-section,
                .status-section,
                .actions {
                    padding-left: 20px;
                    padding-right: 20px;
                }

                .details-title {
                    font-size: 16px;
                }
            }

            .invalid-warning {
                background: linear-gradient(135deg, #fef3c7, #fde68a);
                border: 1px solid #f59e0b;
                border-radius: 10px;
                padding: 15px;
                margin: -5px 25px 20px;
                animation: fadeIn 0.8s ease-out 0.5s both;
            }

            .invalid-warning p {
                margin: 0;
                color: #92400e;
                font-weight: 500;
                font-size: 14px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="payment-card">
                <div class="header">
                    <h1>Payment Result</h1>
                    <p>VNPAY Payment Gateway</p>
                </div>
                
                <c:choose>
                    <c:when test="${!payment.validHash}">
                        <div class="status-section invalid">
                            <div class="status-icon">
                                <i class="fas fa-exclamation-triangle"></i>
                            </div>
                            <h2 class="status-title">Invalid Signature</h2>
                            <p class="status-message">Payment information cannot be verified</p>
                        </div>
                        <div class="invalid-warning">
                            <p>Please contact the administrator for support.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="status-section ${payment.responseCode eq '00' ? 'success' : 'error'}">
                            <div class="status-icon">
                                <i class="fas ${payment.responseCode eq '00' ? 'fa-check' : 'fa-times'}"></i>
                            </div>
                            <h2 class="status-title">${payment.message}</h2>
                            <p class="status-message">
                                ${payment.responseCode eq '00' ? 'Transaction has been processed successfully' : 'Transaction failed, please try again'}
                            </p>
                        </div>
                        
                        <div class="details-section">
                            <h3 class="details-title">
                                <i class="fas fa-receipt"></i>
                                Transaction Details
                            </h3>
                            
                            <div class="detail-item">
                                <span class="detail-label">Transaction ID</span>
                                <span class="detail-value">${payment.transactionId}</span>
                            </div>
                            
                            <div class="detail-item">
                                <span class="detail-label">Amount</span>
                                <span class="detail-value amount-value">
                                    <fmt:formatNumber value="${payment.amount/100}" type="currency" currencySymbol="VND"/>
                                </span>
                            </div>
                            
                            <div class="detail-item">
                                <span class="detail-label">Description</span>
                                <span class="detail-value">${payment.orderInfo}</span>
                            </div>
                            
                            <div class="detail-item">
                                <span class="detail-label">Bank Code</span>
                                <span class="detail-value">${payment.bankCode}</span>
                            </div>
                            
                            <div class="detail-item">
                                <span class="detail-label">VNPAY Transaction ID</span>
                                <span class="detail-value">${payment.vnpayTransactionId}</span>
                            </div>
                            
                            <div class="detail-item">
                                <span class="detail-label">Payment Time</span>
                                <span class="detail-value">${paymentDateStr}</span>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
                
                <div class="actions">
                    <a href="http://localhost:5173" class="btn">
                        <i class="fas fa-home"></i>
                        Back to Home
                    </a>
                </div>
            </div>
            
            <div class="footer">
                <p>&copy; VNPAY <%=java.time.Year.now().getValue()%> - Online Payment Gateway</p>
            </div>
        </div>
    </body>
</html>