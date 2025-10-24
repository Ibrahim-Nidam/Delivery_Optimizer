package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.WarehouseDTO;

import java.util.List;

public interface WarehouseService {

    public WarehouseDTO createWarehouse(WarehouseDTO dto);
    public List<WarehouseDTO> getAllWarehouses();
    public WarehouseDTO getWarehouseById(Long id);
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO dto);
    public void deleteWarehouse(Long id);

}
