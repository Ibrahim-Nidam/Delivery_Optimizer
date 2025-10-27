package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.mapper.TourMapper;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Vehicle;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.model.enums.DeliveryStatus;
import com.deliveryoptimizer.model.enums.OptimizationMethod;
import com.deliveryoptimizer.model.enums.TourStatus;
import com.deliveryoptimizer.repository.DeliveryRepository;
import com.deliveryoptimizer.repository.TourRepository;
import com.deliveryoptimizer.repository.VehicleRepository;
import com.deliveryoptimizer.repository.WarehouseRepository;
import com.deliveryoptimizer.service.interfaces.TourService;
import com.deliveryoptimizer.util.DistanceCalculator;
import com.deliveryoptimizer.util.TourUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TourServiceImpl implements TourService {
    private final TourRepository tourRepository;
    private final DeliveryRepository deliveryRepository;
    private final WarehouseRepository warehouseRepository;
    private final VehicleRepository vehicleRepository;
    private final NearestNeighborOptimizer nearestNeighborOptimizer;
    private final ClarkeWrightOptimizer clarkeWrightOptimizer;
    private final DistanceCalculator distanceCalculator;

    public TourServiceImpl(TourRepository tourRepository, DeliveryRepository deliveryRepository, WarehouseRepository warehouseRepository, VehicleRepository vehicleRepository, NearestNeighborOptimizer nearestNeighborOptimizer, ClarkeWrightOptimizer clarkeWrightOptimizer, DistanceCalculator distanceCalculator){
        this.tourRepository = tourRepository;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
        this.deliveryRepository = deliveryRepository;
        this.nearestNeighborOptimizer = nearestNeighborOptimizer;
        this.clarkeWrightOptimizer = clarkeWrightOptimizer;
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public TourDTO createTour(TourDTO dto){
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle Not Found!"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse Not Found!"));

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

        Tour saved = tourRepository.save(tour);
        deliveries.forEach(d -> d.setTour(saved));
        deliveryRepository.saveAll(deliveries);
        return TourMapper.toDTO(saved);
    }

    @Override
    public List<TourDTO> getAllTours(){
        return tourRepository.findAll().stream()
                .map(TourMapper::toDTO)
                .toList();
    }

    @Override
    public TourDTO getTourById(Long id){
        return tourRepository.findById(id)
                .map(TourMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Tour Not Found!"));
    }

    @Override
    public TourDTO updateTour(Long id, TourDTO dto){
        Tour existingTour = tourRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        boolean vehicleInUse = tourRepository.existsByVehicleId(vehicle.getId())
                && !existingTour.getVehicle().getId().equals(vehicle.getId());
        if (vehicleInUse) {
            throw new RuntimeException("This vehicle is already assigned to another tour");
        }

        boolean warehouseInUse = tourRepository.existsByWarehouseId(warehouse.getId())
                && !existingTour.getWarehouse().getId().equals(warehouse.getId());
        if (warehouseInUse) {
            throw new RuntimeException("This warehouse is already assigned to another tour");
        }

        List<Delivery> deliveries = deliveryRepository.findAllById(dto.getDeliveryIds());

        boolean hasAssignedDeliveries = deliveries.stream()
                .anyMatch(d -> d.getTour() != null && !d.getTour().getId().equals(id));
        if (hasAssignedDeliveries) {
            throw new RuntimeException("One or more deliveries are already assigned to another tour");
        }

        existingTour.getDeliveries().forEach(d -> d.setTour(null));
        deliveryRepository.saveAll(existingTour.getDeliveries());

        existingTour.setDate(dto.getDate());
        existingTour.setVehicle(vehicle);
        existingTour.setWarehouse(warehouse);
        existingTour.setStatus(dto.getStatus());
        existingTour.setDeliveries(deliveries);

        Tour saved = tourRepository.save(existingTour);
        deliveries.forEach(d -> d.setTour(saved));
        deliveryRepository.saveAll(deliveries);

        return TourMapper.toDTO(saved);
    }

    @Override
    public void deleteTour(Long id){
        tourRepository.deleteById(id);
    }

    @Override
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

    @Override
    public List<Long> optimizeTour(Long tourId, OptimizationMethod method){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour Not Found!"));

        List<Delivery> optimized;

        switch (method){
            case NN -> optimized = nearestNeighborOptimizer.optimizerTour(tour);
            case CW -> optimized = clarkeWrightOptimizer.optimizerTour(tour);
            default -> throw new IllegalArgumentException("Unsupported optimization method: " + method);
        }

        tour.setDeliveries(optimized);
        tourRepository.save(tour);

        return optimized.stream()
                .map(Delivery::getId)
                .toList();
    }

    @Override
    public Map<String, String> getTourDistances(Long tourId){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour Not Found!"));

        List<Delivery> nnOrder = nearestNeighborOptimizer.optimizerTour(tour);
        double nnDistance = TourUtils.calculateTotalDistance(tour.getWarehouse(), nnOrder, distanceCalculator);

        List<Delivery> cwOrder = clarkeWrightOptimizer.optimizerTour(tour);
        double cwDistance = TourUtils.calculateTotalDistance(tour.getWarehouse(), cwOrder, distanceCalculator);

        Map<String, String> distances = new LinkedHashMap<>();
        distances.put("Nearest Neighbor", TourUtils.formatDistance(nnDistance));
        distances.put("Clarke Wright", TourUtils.formatDistance(cwDistance));

        return distances;
    }
}
