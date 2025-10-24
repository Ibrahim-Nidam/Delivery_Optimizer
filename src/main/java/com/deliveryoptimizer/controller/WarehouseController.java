package com.deliveryoptimizer.controller;

import com.deliveryoptimizer.dto.WarehouseDTO;
import com.deliveryoptimizer.service.interfaces.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService){
        this.warehouseService = warehouseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WarehouseDTO createWarehouse(@Valid @RequestBody WarehouseDTO dto){
        return warehouseService.createWarehouse(dto);
    }

    @GetMapping
    public List<WarehouseDTO> getAllWarehouses(){
        return warehouseService.getAllWarehouses();
    }

    @GetMapping("/{id}")
    public WarehouseDTO getWarehouseById(@PathVariable Long id){
        return warehouseService.getWarehouseById(id);
    }

    @PutMapping("/{id}")
    public WarehouseDTO updateWarehouse(@PathVariable Long id, @Valid @RequestBody WarehouseDTO dto){
        return warehouseService.updateWarehouse(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWarehouse(@PathVariable Long id){
        warehouseService.deleteWarehouse(id);
    }
}
