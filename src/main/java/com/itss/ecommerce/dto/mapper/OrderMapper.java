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
    public OrderItemListDTO toDTO(OrderItemList order) {
        if (order == null) return null;
        
        OrderItemListDTO dto = new OrderItemListDTO();
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
            dto.setOrderItems(order.getOrderItems().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }
        
        if (order.getInvoice() != null) {
            dto.setInvoice(toDTO(order.getInvoice()));
        }
        
        return dto;
    }
    
    /**
     * Convert OrderItem entity to OrderItemDTO
     */
    public OrderItemDTO toDTO(OrderItem orderItem) {
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
            dto.setProduct(productMapper.toDTO(orderItem.getProduct()));
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
    public OrderItemList toEntity(OrderItemListDTO dto) {
        if (dto == null) return null;
        
        OrderItemList order = new OrderItemList();
        order.setOrderId(dto.getOrderId());
        order.setTotalBeforeVat(dto.getTotalBeforeVat());
        order.setTotalAfterVat(dto.getTotalAfterVat());
        if (dto.getStatus() != null) {
            order.setStatus(OrderItemList.OrderStatus.valueOf(dto.getStatus()));
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
        deliveryInfo.setWard(dto.getWard());
        deliveryInfo.setDistrict(dto.getDistrict());
        deliveryInfo.setProvince(dto.getProvince());
        deliveryInfo.setDeliveryMessage(dto.getDeliveryMessage());
        deliveryInfo.setDeliveryFee(dto.getDeliveryFee());
        
        return deliveryInfo;
    }
    
    /**
     * Convert OrderItemDTO to OrderItem entity
     */
    public OrderItem toEntity(OrderItemDTO dto) {
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
    public List<OrderItemListDTO> toDTOList(List<OrderItemList> orders) {
        if (orders == null) return null;
        return orders.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of OrderItem entities to OrderItemDTOs
     */
    public List<OrderItemDTO> toOrderItemDTOList(List<OrderItem> orderItems) {
        if (orderItems == null) return null;
        return orderItems.stream()
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