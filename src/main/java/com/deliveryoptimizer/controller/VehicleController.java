package com.deliveryoptimizer.controller;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.service.impl.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public VehicleDTO createVehicle(@RequestBody VehicleDTO dto){
        return vehicleService.createVehicle(dto);
    }

    @GetMapping
    public List<VehicleDTO> getAllVehicles(){
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/{id}")
    public VehicleDTO getVehicleById(@PathVariable Long id){
        return vehicleService.getVehicleById(id);
    }

    @PutMapping("/{id}")
    public VehicleDTO updateVehicle(@PathVariable Long id, @RequestBody VehicleDTO dto){
        return vehicleService.updateVehicle(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable Long id){
        vehicleService.deleteVehicle(id);
    }
}
