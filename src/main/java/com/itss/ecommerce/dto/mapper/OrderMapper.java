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
    /**
     * Convert Order entity to OrderDTO
     */
    public static OrderDTO toDTO(Order order) {
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
        if (order.getDeliveryInformation() != null) {
            dto.setDeliveryInfo(toDTO(order.getDeliveryInformation()));
        }
        
        if (order.getOrderItems() != null) {
            List<OrderItemDTO> orderItems = order.getOrderItems().stream()
                    .map(OrderMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(orderItems);
        }
        
        if (order.getInvoice() != null) {
            dto.setInvoice(toDTO(order.getInvoice()));
        }
        
        return dto;
    }
    
    /**
     * Convert OrderItem entity to OrderItemDTO
     */
    public static OrderItemDTO toDTO(OrderItem orderItem) {
        if (orderItem == null) return null;
        
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItemId(orderItem.getOrderItemId());
        dto.setOrderId(orderItem.getOrder() != null ? orderItem.getOrder().getOrderId() : null);
        dto.setStatus(orderItem.getStatus().name());
        dto.setRushOrder(orderItem.getRushOrder());
        dto.setQuantity(orderItem.getQuantity());
        dto.setTotalFee(orderItem.getTotalFee());
        dto.setDeliveryTime(orderItem.getDeliveryTime());
        dto.setInstructions(orderItem.getInstructions());
        
        // Convert product
        if (orderItem.getProduct() != null) {
            dto.setProduct(ProductMapper.toDTO(orderItem.getProduct()));
        }
        
        return dto;
    }
    
    /**
     * Convert DeliveryInformation entity to DeliveryInformationDTO
     */
    public static DeliveryInformationDTO toDTO(DeliveryInformation deliveryInfo) {
        if (deliveryInfo == null) return null;
        
        DeliveryInformationDTO dto = new DeliveryInformationDTO();
        dto.setDeliveryId(deliveryInfo.getDeliveryId());
        dto.setName(deliveryInfo.getName());
        dto.setPhone(deliveryInfo.getPhone());
        dto.setEmail(deliveryInfo.getEmail());
        dto.setAddress(deliveryInfo.getAddress());
        dto.setDistrict(deliveryInfo.getDistrict());
        dto.setWard(deliveryInfo.getWard());
        dto.setProvince(deliveryInfo.getProvince());
        dto.setDeliveryMessage(deliveryInfo.getDeliveryMessage());
        dto.setDeliveryFee(deliveryInfo.getDeliveryFee());
        
        return dto;
    }
    
    /**
     * Convert Invoice entity to InvoiceDTO
     */
    public static InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;
        
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());
        dto.setOrderId(invoice.getOrder() != null ? invoice.getOrder().getOrderId() : null);
        dto.setTransactionId(invoice.getSuccessfulTransaction() != null ? 
                invoice.getSuccessfulTransaction().getTransactionId() : null);
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
    public static Order toEntity(OrderDTO dto) {
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
    public static DeliveryInformation toEntity(DeliveryInformationDTO dto) {
        if (dto == null) return null;
        
        DeliveryInformation deliveryInfo = new DeliveryInformation();
        deliveryInfo.setDeliveryId(dto.getDeliveryId());
        deliveryInfo.setName(dto.getName());
        deliveryInfo.setPhone(dto.getPhone());
        deliveryInfo.setEmail(dto.getEmail());
        deliveryInfo.setAddress(dto.getAddress());
        deliveryInfo.setWard(dto.getWard());
        deliveryInfo.setDistrict(dto.getDistrict());
        deliveryInfo.setProvince(dto.getProvince());
        deliveryInfo.setDeliveryMessage(dto.getDeliveryMessage());
        deliveryInfo.setDeliveryFee(dto.getDeliveryFee());
        deliveryInfo.setDeliveryTime(dto.getDeliveryTime());
        deliveryInfo.setRushDeliveryInstruction(dto.getRushDeliveryInstruction());
        return deliveryInfo;
    }
    
    /**
     * Convert OrderItemDTO to OrderItem entity
     */
    public static OrderItem toEntity(OrderItemDTO dto) {
        if (dto == null) return null;
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(dto.getOrderItemId());
        if (dto.getStatus() != null) {
            orderItem.setStatus(OrderItem.OrderItemStatus.valueOf(dto.getStatus()));
        }
        orderItem.setRushOrder(dto.getRushOrder());
        orderItem.setQuantity(dto.getQuantity());
        orderItem.setTotalFee(dto.getTotalFee());
        orderItem.setDeliveryTime(dto.getDeliveryTime());
        orderItem.setInstructions(dto.getInstructions());
        
        return orderItem;
    }
    
    /**
     * Convert list of Order entities to OrderDTOs
     */
    public static List<OrderDTO> toDTOList(List<Order> orders) {
        if (orders == null) return null;
        return orders.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of OrderItem entities to OrderItemDTOs
     */
    public static List<OrderItemDTO> toOrderItemDTOList(List<OrderItem> orderItems) {
        if (orderItems == null) return null;
        return orderItems.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of DeliveryInformation entities to DeliveryInformationDTOs
     */
    public static List<DeliveryInformationDTO> toDeliveryInfoDTOList(List<DeliveryInformation> deliveryInfos) {
        if (deliveryInfos == null) return null;
        return deliveryInfos.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of Invoice entities to InvoiceDTOs
     */
    public static List<InvoiceDTO> toInvoiceDTOList(List<Invoice> invoices) {
        if (invoices == null) return null;
        return invoices.stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }
}