package com.deliveryoptimizer.mapper;

import com.deliveryoptimizer.dto.WarehouseDTO;
import com.deliveryoptimizer.model.Warehouse;

public class WarehouseMapper {
    public static WarehouseDTO toDTO(Warehouse warehouse){
        if(warehouse == null) return null;
        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .address(warehouse.getAddress())
                .altitude(warehouse.getAltitude())
                .longitude(warehouse.getLongitude())
                .openTime(warehouse.getOpenTime())
                .closeTime(warehouse.getCloseTime())
                .build();
    }

    public static Warehouse toEntity(WarehouseDTO dto){
        if(dto == null) return null;
        return Warehouse.builder()
                .id(dto.getId())
                .address(dto.getAddress())
                .altitude(dto.getAltitude())
                .longitude(dto.getLongitude())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .build();
    }
}
