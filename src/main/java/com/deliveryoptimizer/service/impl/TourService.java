package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.mapper.TourMapper;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Vehicle;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.repository.DeliveryRepository;
import com.deliveryoptimizer.repository.TourRepository;
import com.deliveryoptimizer.repository.VehicleRepository;
import com.deliveryoptimizer.repository.WarehouseRepository;

import java.util.List;

public class TourService {
    private final TourRepository tourRepository;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseRepository warehouseRepository;
    private final VehicleRepository vehicleRepository;

    public TourService(TourRepository tourRepository, DeliveryRepository deliveryRepository, WarehouseRepository warehouseRepository, VehicleRepository vehicleRepository){
        this.tourRepository = tourRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public TourDTO createTour(TourDTO dto){
        Tour tour = TourMapper.toEntity(dto);
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found!"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse Not Found!"));

        List<Delivery> deliveries = deliveryRepository.findAllById(dto.getDeliveryIds());

        tour.setDeliveries(deliveries);
        tour.setVehicle(vehicle);
        tour.setWarehouse(warehouse);


        Tour saved = tourRepository.save(tour);
        return TourMapper.toDTO(saved);
    }

    public List<TourDTO> getAllTours(){
        return tourRepository.findAll().stream()
                .map(TourMapper::toDTO)
                .toList();
    }

    public TourDTO getTourById(Long id){
        return tourRepository.findById(id)
                .map(TourMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Tour Not Found!"));
    }

    public TourDTO updateTour(Long id, TourDTO dto){
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour Not Found!"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                        .orElseThrow(() -> new RuntimeException("Vehicle Not Found!"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                        .orElseThrow(() -> new RuntimeException("Warehouse Not Found!"));

        List<Delivery> deliveries = deliveryRepository.findAllById(dto.getDeliveryIds());

        tour.setDate(dto.getDate());
        tour.setDeliveries(deliveries);
        tour.setStatus(dto.getStatus());
        tour.setVehicle(vehicle);
        tour.setWarehouse(warehouse);
        tour.setTotalDistance(dto.getTotalDistance());

        Tour saved = tourRepository.save(tour);

        return TourMapper.toDTO(saved);
    }

    public void deleteTour(Long id){
        tourRepository.deleteById(id);
    }
}
