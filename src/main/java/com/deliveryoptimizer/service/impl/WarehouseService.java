package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.WarehouseDTO;
import com.deliveryoptimizer.mapper.WarehouseMapper;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.repository.WarehouseRepository;

import java.util.List;

public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }

    public WarehouseDTO createWarehouse(WarehouseDTO dto){
        Warehouse warehouse = WarehouseMapper.toEntity(dto);
        Warehouse saved = warehouseRepository.save(warehouse);
        return WarehouseMapper.toDTO(saved);
    }

    public List<WarehouseDTO> getAllWarehouses(){
        return warehouseRepository.findAll().stream()
                .map(WarehouseMapper::toDTO)
                .toList();
    }

    public WarehouseDTO getWarehouseById(Long id){
        return warehouseRepository.findById(id)
                .map(WarehouseMapper::toDTO)
                .orElseThrow(()-> new RuntimeException("Warehouse Not Found !"));
    }

    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto){
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Warehouse Not Found!"));

        warehouse.setAddress(dto.getAddress());
        warehouse.setAltitude(dto.getAltitude());
        warehouse.setLongitude(dto.getLongitude());
        warehouse.setOpenTime(dto.getOpenTime());
        warehouse.setCloseTime(dto.getCloseTime());

        Warehouse saved = warehouseRepository.save(warehouse);

        return WarehouseMapper.toDTO(saved);
    }

    public void deleteWarehouse(Long id){
        warehouseRepository.deleteById(id);
    }
}
