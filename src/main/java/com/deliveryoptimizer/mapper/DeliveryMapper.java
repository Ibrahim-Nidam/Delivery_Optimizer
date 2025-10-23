package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;

public class DeliveryMapper {
    public static DeliveryDTO toDTO(Delivery delivery){
        if(delivery == null) return null;
        return DeliveryDTO.builder()
                .id(delivery.getId())
                .tourId(delivery.getTour() != null ? delivery.getTour().getId() : null)
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
        Delivery.DeliveryBuilder builder =  Delivery.builder()
                .id(dto.getId())
                .altitude(dto.getAltitude())
                .longitude(dto.getLongitude())
                .maxWeight(dto.getMaxWeight())
                .maxVolume(dto.getMaxVolume())
                .timeSlot(dto.getTimeSlot())
                .status(dto.getStatus());

        if(dto.getTourId() != null){
            builder.tour(Tour.builder()
                    .id(dto.getTourId())
                            .build()
                    );
        }

        return builder.build();
    }
}
