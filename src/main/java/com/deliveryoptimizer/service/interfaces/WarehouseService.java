package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.WarehouseDTO;

import java.util.List;

public interface WarehouseService {

    WarehouseDTO createWarehouse(WarehouseDTO dto);
    List<WarehouseDTO> getAllWarehouses();
    WarehouseDTO getWarehouseById(Long id);
    WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto);
    void deleteWarehouse(Long id);

}
