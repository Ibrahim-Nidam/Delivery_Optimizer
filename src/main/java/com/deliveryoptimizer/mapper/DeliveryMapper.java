package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.model.Delivery;

public class DeliveryMapper {
    public static DeliveryDTO toDTO(Delivery delivery){
        if(delivery == null) return null;
        return DeliveryDTO.builder()
                .id(delivery.getId())
                .altitude(delivery.getAltitude())
                .longitude(delivery.getLongitude())
                .maxVolume(delivery.getMaxVolume())
                .maxWeight(delivery.getMaxWeight())
                .timeSlot(delivery.getTimeSlot())
                .status(delivery.getStatus())
                .build();
    }

    public static Delivery toEntity(DeliveryDTO dto){
        if(dto == null) return null;
        return Delivery.builder()
                .id(dto.getId())
                .altitude(dto.getAltitude())
                .longitude(dto.getLongitude())
                .maxWeight(dto.getMaxWeight())
                .maxVolume(dto.getMaxVolume())
                .timeSlot(dto.getTimeSlot())
                .status(dto.getStatus())
                .build();
    }
}
