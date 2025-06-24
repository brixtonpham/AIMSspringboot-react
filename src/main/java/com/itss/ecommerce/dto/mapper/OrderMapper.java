package com.itss.ecommerce.dto.mapper;

import com.itss.ecommerce.dto.*;
import com.itss.ecommerce.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    
    private final ProductMapper productMapper;
    
    /**
     * Convert Order entity to OrderDTO
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) return null;
        
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setTotalBeforeVat(order.getTotalBeforeVat());
        dto.setTotalAfterVat(order.getTotalAfterVat());
        dto.setStatus(order.getStatus().name());
        dto.setVatPercentage(order.getVatPercentage());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        // Convert related entities
        if (order.getDeliveryInfo() != null) {
            dto.setDeliveryInfo(toDTO(order.getDeliveryInfo()));
        }
        
        if (order.getOrderLines() != null) {
            dto.setOrderLines(order.getOrderLines().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }
        
        if (order.getInvoice() != null) {
            dto.setInvoice(toDTO(order.getInvoice()));
        }
        
        return dto;
    }
    
    /**
     * Convert OrderLine entity to OrderLineDTO
     */
    public OrderLineDTO toDTO(OrderLine orderLine) {
        if (orderLine == null) return null;
        
        OrderLineDTO dto = new OrderLineDTO();
        dto.setOrderLineId(orderLine.getOrderLineId());
        dto.setOrderId(orderLine.getOrder() != null ? orderLine.getOrder().getOrderId() : null);
        dto.setStatus(orderLine.getStatus().name());
        dto.setRushOrder(orderLine.getRushOrder());
        dto.setQuantity(orderLine.getQuantity());
        dto.setTotalFee(orderLine.getTotalFee());
        dto.setDeliveryTime(orderLine.getDeliveryTime());
        dto.setInstructions(orderLine.getInstructions());
        
        // Convert product
        if (orderLine.getProduct() != null) {
            dto.setProduct(productMapper.toDTO(orderLine.getProduct()));
        }
        
        return dto;
    }
    
    /**
     * Convert DeliveryInformation entity to DeliveryInformationDTO
     */
    public DeliveryInformationDTO toDTO(DeliveryInformation deliveryInfo) {
        if (deliveryInfo == null) return null;
        
        DeliveryInformationDTO dto = new DeliveryInformationDTO();
        dto.setDeliveryId(deliveryInfo.getDeliveryId());
        dto.setName(deliveryInfo.getName());
        dto.setPhone(deliveryInfo.getPhone());
        dto.setEmail(deliveryInfo.getEmail());
        dto.setAddress(deliveryInfo.getAddress());
        dto.setProvince(deliveryInfo.getProvince());
        dto.setDeliveryMessage(deliveryInfo.getDeliveryMessage());
        dto.setDeliveryFee(deliveryInfo.getDeliveryFee());
        
        return dto;
    }
    
    /**
     * Convert Invoice entity to InvoiceDTO
     */
    public InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;
        
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setOrderId(invoice.getOrder() != null ? invoice.getOrder().getOrderId() : null);
        dto.setTransactionId(invoice.getTransactionId());
        dto.setDescription(invoice.getDescription());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setPaymentStatus(invoice.getPaymentStatus().name());
        dto.setPaymentMethod(invoice.getPaymentMethod());
        dto.setPaidAt(invoice.getPaidAt());
        dto.setTotalAmount(invoice.getTotalAmount());
        
        return dto;
    }
    
    /**
     * Convert OrderDTO to Order entity
     */
    public Order toEntity(OrderDTO dto) {
        if (dto == null) return null;
        
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setTotalBeforeVat(dto.getTotalBeforeVat());
        order.setTotalAfterVat(dto.getTotalAfterVat());
        if (dto.getStatus() != null) {
            order.setStatus(Order.OrderStatus.valueOf(dto.getStatus()));
        }
        order.setVatPercentage(dto.getVatPercentage());
        order.setCreatedAt(dto.getCreatedAt());
        order.setUpdatedAt(dto.getUpdatedAt());
        
        return order;
    }
    
    /**
     * Convert DeliveryInformationDTO to DeliveryInformation entity
     */
    public DeliveryInformation toEntity(DeliveryInformationDTO dto) {
        if (dto == null) return null;
        
        DeliveryInformation deliveryInfo = new DeliveryInformation();
        deliveryInfo.setDeliveryId(dto.getDeliveryId());
        deliveryInfo.setName(dto.getName());
        deliveryInfo.setPhone(dto.getPhone());
        deliveryInfo.setEmail(dto.getEmail());
        deliveryInfo.setAddress(dto.getAddress());
        deliveryInfo.setProvince(dto.getProvince());
        deliveryInfo.setDeliveryMessage(dto.getDeliveryMessage());
        deliveryInfo.setDeliveryFee(dto.getDeliveryFee());
        
        return deliveryInfo;
    }
    
    /**
     * Convert OrderLineDTO to OrderLine entity
     */
    public OrderLine toEntity(OrderLineDTO dto) {
        if (dto == null) return null;
        
        OrderLine orderLine = new OrderLine();
        orderLine.setOrderLineId(dto.getOrderLineId());
        if (dto.getStatus() != null) {
            orderLine.setStatus(OrderLine.OrderLineStatus.valueOf(dto.getStatus()));
        }
        orderLine.setRushOrder(dto.getRushOrder());
        orderLine.setQuantity(dto.getQuantity());
        orderLine.setTotalFee(dto.getTotalFee());
        orderLine.setDeliveryTime(dto.getDeliveryTime());
        orderLine.setInstructions(dto.getInstructions());
        
        return orderLine;
    }
    
    /**
     * Convert list of Order entities to OrderDTOs
     */
    public List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) return null;
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of OrderLine entities to OrderLineDTOs
     */
    public List<OrderLineDTO> toOrderLineDTOList(List<OrderLine> orderLines) {
        if (orderLines == null) return null;
        return orderLines.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of DeliveryInformation entities to DeliveryInformationDTOs
     */
    public List<DeliveryInformationDTO> toDeliveryInfoDTOList(List<DeliveryInformation> deliveryInfos) {
        if (deliveryInfos == null) return null;
        return deliveryInfos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of Invoice entities to InvoiceDTOs
     */
    public List<InvoiceDTO> toInvoiceDTOList(List<Invoice> invoices) {
        if (invoices == null) return null;
        return invoices.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}