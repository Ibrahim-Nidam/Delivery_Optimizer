package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.mapper.TourMapper;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Vehicle;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.model.enums.DeliveryStatus;
import com.deliveryoptimizer.model.enums.TourStatus;
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
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found!"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse Not Found!"));

        // 🔹 Check if warehouse already used (optional rule)
        if (tourRepository.existsByWarehouseId(warehouse.getId())) {
            throw new RuntimeException("This warehouse is already assigned to another tour");
        }

        List<Delivery> deliveries = deliveryRepository.findAllById(dto.getDeliveryIds());

        boolean hasAssignedDeliveries = deliveries.stream()
                .anyMatch(d -> d.getTour() != null);
        if (hasAssignedDeliveries) {
            throw new RuntimeException("One or more deliveries are already assigned to another tour");
        }

        Tour tour = TourMapper.toEntity(dto);
        tour.setVehicle(vehicle);
        tour.setWarehouse(warehouse);
        tour.setDeliveries(deliveries);
        tour.setStatus(TourStatus.PLANNED);

        // 🔹 Save and update deliveries
        Tour saved = tourRepository.save(tour);
        deliveries.forEach(d -> d.setTour(saved));
        deliveryRepository.saveAll(deliveries);
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
        Tour existingTour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        // 🔹 Ensure vehicle isn’t used by another tour
        boolean vehicleInUse = tourRepository.existsByVehicleId(vehicle.getId())
                && !existingTour.getVehicle().getId().equals(vehicle.getId());
        if (vehicleInUse) {
            throw new RuntimeException("This vehicle is already assigned to another tour");
        }

        // 🔹 Ensure warehouse isn’t used by another tour
        boolean warehouseInUse = tourRepository.existsByWarehouseId(warehouse.getId())
                && !existingTour.getWarehouse().getId().equals(warehouse.getId());
        if (warehouseInUse) {
            throw new RuntimeException("This warehouse is already assigned to another tour");
        }

        // 🔹 Handle deliveries
        List<Delivery> deliveries = deliveryRepository.findAllById(dto.getDeliveryIds());

        boolean hasAssignedDeliveries = deliveries.stream()
                .anyMatch(d -> d.getTour() != null && !d.getTour().getId().equals(id));
        if (hasAssignedDeliveries) {
            throw new RuntimeException("One or more deliveries are already assigned to another tour");
        }

        // 🔹 Unlink old deliveries
        existingTour.getDeliveries().forEach(d -> d.setTour(null));
        deliveryRepository.saveAll(existingTour.getDeliveries());

        // 🔹 Update fields
        existingTour.setDate(dto.getDate());
        existingTour.setVehicle(vehicle);
        existingTour.setWarehouse(warehouse);
        existingTour.setStatus(dto.getStatus());
        existingTour.setDeliveries(deliveries);

        // 🔹 Save tour + deliveries
        Tour saved = tourRepository.save(existingTour);
        deliveries.forEach(d -> d.setTour(saved));
        deliveryRepository.saveAll(deliveries);

        return TourMapper.toDTO(saved);
    }

    public void deleteTour(Long id){
        tourRepository.deleteById(id);
    }

    public TourDTO addDeliveriesToTour(Long tourId, List<Long> deliveryIds){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour Not Found!"));

        List<Delivery> deliveries = deliveryRepository.findAllById(deliveryIds);

        if(deliveries.size() != deliveryIds.size()){
            throw new RuntimeException("One or More Deliveries Not found!");
        }

        boolean alreadyAssigned = deliveries.stream()
                .anyMatch(a -> a.getTour() != null);
        if(alreadyAssigned){
            throw new RuntimeException("One or more deliveries are already assigned to a tour");
        }

        Vehicle vehicle = tour.getVehicle();
        if(vehicle == null){
            throw new RuntimeException("Tour has no vehicle assigned");
        }

        double totalWeight = tour.getDeliveries().stream()
                .mapToDouble(Delivery::getMaxWeight).sum() +
                deliveries.stream()
                        .mapToDouble(Delivery::getMaxWeight).sum();

        double totalVolume = tour.getDeliveries().stream()
                .mapToDouble(Delivery::getMaxVolume).sum() +
                deliveries.stream()
                        .mapToDouble(Delivery::getMaxVolume).sum();

        int totalDeliveries = tour.getDeliveries().size() + deliveries.size();

        if(totalWeight > vehicle.getMaxWeight()){
            throw new RuntimeException("Vehicle max weight exceeded");
        }
        if(totalVolume > vehicle.getMaxVolume()){
            throw new RuntimeException("Vehicle max Volume exceeded");
        }
        if(totalDeliveries > vehicle.getMaxDeliveries()){
            throw new RuntimeException("Vehicle max Deliveries exceeded");
        }

        deliveries.forEach(d -> {
            d.setTour(tour);
            d.setStatus(DeliveryStatus.IN_TRANSIT);
        });
        tour.getDeliveries().addAll(deliveries);

        tourRepository.save(tour);
        deliveryRepository.saveAll(deliveries);

        return TourMapper.toDTO(tour);
    }
}
