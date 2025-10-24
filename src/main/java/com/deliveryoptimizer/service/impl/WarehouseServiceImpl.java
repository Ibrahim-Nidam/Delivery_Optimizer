package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.WarehouseDTO;
import com.deliveryoptimizer.mapper.WarehouseMapper;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.repository.WarehouseRepository;
import com.deliveryoptimizer.service.interfaces.WarehouseService;

import java.util.List;

public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository){
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseDTO createWarehouse(WarehouseDTO dto){
        Warehouse warehouse = WarehouseMapper.toEntity(dto);
        Warehouse saved = warehouseRepository.save(warehouse);
        return WarehouseMapper.toDTO(saved);
    }

    @Override
    public List<WarehouseDTO> getAllWarehouses(){
        return warehouseRepository.findAll().stream()
                .map(WarehouseMapper::toDTO)
                .toList();
    }

    @Override
    public WarehouseDTO getWarehouseById(Long id){
        return warehouseRepository.findById(id)
                .map(WarehouseMapper::toDTO)
                .orElseThrow(()-> new RuntimeException("Warehouse Not Found !"));
    }

    @Override
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

    @Override
    public void deleteWarehouse(Long id){
        warehouseRepository.deleteById(id);
    }
}
