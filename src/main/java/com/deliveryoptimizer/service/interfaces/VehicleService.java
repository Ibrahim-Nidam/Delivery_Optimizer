package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.mapper.VehicleMapper;
import com.deliveryoptimizer.model.Vehicle;

import java.util.List;

public interface VehicleService {

    VehicleDTO createVehicle(VehicleDTO dto);
    List<VehicleDTO> getAllVehicles();
    VehicleDTO getVehicleById(Long id);
    VehicleDTO updateVehicle(Long id, VehicleDTO dto);
    void deleteVehicle(Long id);

}
