package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.model.Vehicle;

public class VehicleMapper {
    public static VehicleDTO toDTO(Vehicle vehicle){
        if(vehicle == null) return null;

        return VehicleDTO.builder()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .type(vehicle.getType())
                .maxWeight(vehicle.getMaxWeight())
                .maxVolume(vehicle.getMaxVolume())
                .maxDeliveries(vehicle.getMaxDeliveries())
                .build();
    }

    public static Vehicle toEntity(VehicleDTO dto){
        if(dto == null) return null;
        return Vehicle.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .build();
    }
}
