package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.mapper.VehicleMapper;
import com.deliveryoptimizer.model.Vehicle;

import java.util.List;

public interface VehicleService {

    public VehicleDTO createVehicle(VehicleDTO dto);
    public List<VehicleDTO> getAllVehicles();
    public VehicleDTO getVehicleById(Long id);
    public VehicleDTO updateVehicle(Long id, VehicleDTO dto);
    public void deleteVehicle(Long id);

}
