package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.VehicleDTO;
import com.deliveryoptimizer.mapper.VehicleMapper;
import com.deliveryoptimizer.model.Vehicle;
import com.deliveryoptimizer.repository.VehicleRepository;

import java.util.List;

public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository){
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleDTO createVehicle(VehicleDTO dto){
        Vehicle vehicle = VehicleMapper.toEntity(dto);
        Vehicle saved = vehicleRepository.save(vehicle);
        return VehicleMapper.toDTO(saved);
    }

    public List<VehicleDTO> getAllVehicles(){
        return vehicleRepository.findAll().stream()
                .map(VehicleMapper::toDTO)
                .toList();

    }

    public VehicleDTO getVehicleById(Long id){
        return vehicleRepository.findById(id)
                .map(VehicleMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found !"));
    }

    public VehicleDTO updateVehicle(Long id, VehicleDTO dto){
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found !"));

        vehicle.setName(dto.getName());
        vehicle.setType(dto.getType());
        vehicle.setMaxWeight(dto.getMaxWeight());
        vehicle.setMaxVolume(dto.getMaxVolume());
        vehicle.setMaxDeliveries(dto.getMaxDeliveries());

        Vehicle saved = vehicleRepository.save(vehicle);

        return VehicleMapper.toDTO(saved);
    }

    public void deleteVehicle(Long id){
        vehicleRepository.deleteById(id);
    }
}
