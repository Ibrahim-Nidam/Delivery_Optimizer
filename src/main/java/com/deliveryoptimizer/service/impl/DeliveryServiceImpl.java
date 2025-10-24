package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.mapper.DeliveryMapper;
import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.repository.DeliveryRepository;
import com.deliveryoptimizer.service.interfaces.DeliveryService;

import java.util.List;

public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryServiceImpl(DeliveryRepository deliveryRepository){
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public DeliveryDTO createDelivery(DeliveryDTO dto){
        Delivery delivery = DeliveryMapper.toEntity(dto);
        Delivery saved = deliveryRepository.save(delivery);
        return DeliveryMapper.toDTO(saved);
    }

    @Override
    public List<DeliveryDTO> getAllDeliveries(){
        return deliveryRepository.findAll().stream()
                .map(DeliveryMapper::toDTO)
                .toList();
    }

    @Override
    public DeliveryDTO getDeliveryById(Long id){
        return deliveryRepository.findById(id)
                .map(DeliveryMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Delivery Not Found !"));
    }

    @Override
    public DeliveryDTO updateDelivery(Long id, DeliveryDTO dto){
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery Not Found !"));

        delivery.setAltitude(dto.getAltitude());
        delivery.setLongitude(dto.getLongitude());
        delivery.setMaxVolume(dto.getMaxVolume());
        delivery.setMaxWeight(dto.getMaxWeight());
        delivery.setStatus(dto.getStatus());
        delivery.setTimeSlot(dto.getTimeSlot());

        Delivery saved = deliveryRepository.save(delivery);

        return DeliveryMapper.toDTO(saved);
    }

    @Override
    public void deleteDelivery(Long id){
        deliveryRepository.deleteById(id);
    }
}
