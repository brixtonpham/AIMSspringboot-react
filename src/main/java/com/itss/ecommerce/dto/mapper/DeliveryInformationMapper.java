package com.itss.ecommerce.dto.mapper;

import com.itss.ecommerce.dto.DeliveryInformationDTO;
import com.itss.ecommerce.entity.DeliveryInformation;

public class DeliveryInformationMapper {
    /**
     * Convert DeliveryInformationDTO to DeliveryInformation entity
     */
    public static DeliveryInformation toEntity(DeliveryInformationDTO dto) {
        if (dto == null)
            return null;

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
}
