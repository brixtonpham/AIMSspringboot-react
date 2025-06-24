package Project_ITSS.PlaceOrder.Controller;

import Project_ITSS.PlaceOrder.Entity.Order;
import Project_ITSS.PlaceOrder.Entity.DeliveryInformation;
import Project_ITSS.PlaceOrder.Repository.OrderRepository_PlaceOrder;
import Project_ITSS.vnpay.common.entity.TransactionInfo;
import Project_ITSS.vnpay.common.repository.TransactionRepository;
import Project_ITSS.vnpay.common.dto.RefundRequest;
import Project_ITSS.vnpay.common.service.VNPayService;
import Project_ITSS.PlaceOrder.Service.NonDBService_PlaceOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/order")
public class CancelOrderController {
    @Autowired
    private OrderRepository_PlaceOrder orderRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private NonDBService_PlaceOrder mailService;

    @PostMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestParam("order_id") long orderId, HttpServletRequest request) {
        try {
            Order order = orderRepository.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }
            if (!"pending".equalsIgnoreCase(order.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Order cannot be cancelled. Only pending orders can be cancelled.");
            }
            System.out.println("order.getStatus() = '" + order.getStatus() + "'");
            orderRepository.updateOrderStatus(orderId, "cancelled");
            TransactionInfo transaction = transactionRepository.findByOrderId(String.valueOf(orderId));
            if (transaction == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No transaction found for this order. Cannot refund.");
            }
            RefundRequest refundRequest = new RefundRequest();
            refundRequest.setOrderId(transaction.getOrderId());
            refundRequest.setAmount(transaction.getAmount().intValue());
            refundRequest.setTransDate(transaction.getPayDate());
            refundRequest.setTranType("02"); // 02: Hoàn toàn bộ giao dịch
            refundRequest.setUser("admin"); // hoặc lấy user thực hiện
            var refundResponse = vnPayService.refundTransaction(refundRequest, request);
            DeliveryInformation deliveryInfo = orderRepository.getDeliveryInformationById(order.getDelivery_id());
            // Bỏ qua gửi email khi test local
            // if (deliveryInfo != null && deliveryInfo.getEmail() != null) {
            //     String subject = "Thông báo hủy đơn hàng";
            //     String content = "Đơn hàng của bạn đã được hủy và sẽ được hoàn tiền trong thời gian sớm nhất.";
            //     mailService.SendSuccessEmail(deliveryInfo.getEmail(), subject, content);
            // }
            return ResponseEntity.ok(refundResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "OK";
    }
} 