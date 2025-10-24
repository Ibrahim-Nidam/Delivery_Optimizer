package com.deliveryoptimizer.controller;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.service.interfaces.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleDTO createVehicle(@Valid @RequestBody VehicleDTO dto){
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
    public VehicleDTO updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleDTO dto){
        return vehicleService.updateVehicle(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable Long id){
        vehicleService.deleteVehicle(id);
    }
}
