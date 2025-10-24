package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.DeliveryDTO;

import java.util.List;

public interface DeliveryService {

    DeliveryDTO createDelivery(DeliveryDTO dto);
    List<DeliveryDTO> getAllDeliveries();
    DeliveryDTO getDeliveryById(Long id);
    DeliveryDTO updateDelivery(Long id, DeliveryDTO dto);
    void deleteDelivery(Long id);

}
