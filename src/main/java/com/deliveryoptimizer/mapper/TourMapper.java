package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;

public class TourMapper {
    public static TourDTO toDTO(Tour tour){
        if(tour == null) return null;
        return TourDTO.builder()
                .id(tour.getId())
                .date(tour.getDate())
                .totalDistance(tour.getTotalDistance())
                .status(tour.getStatus())
                .vehicleId(tour.getVehicle() != null ? tour.getVehicle().getId() : null)
                .deliveryIds(tour.getDeliveries() != null ? tour.getDeliveries().stream()
                        .map(Delivery::getId)
                        .toList()
                        : null)
                .warehouseId(tour.getWarehouse() != null ? tour.getWarehouse().getId() : null)
                .build();
    }

    public static Tour toEntity(TourDTO dto){
        if(dto == null) return null;
        return Tour.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .totalDistance(dto.getTotalDistance())
                .status(dto.getStatus())
                .build();
    }
}
