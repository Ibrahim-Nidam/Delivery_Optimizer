package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.DeliveryDTO;

import java.util.List;

public interface DeliveryService {

    public DeliveryDTO createDelivery(DeliveryDTO dto);
    public List<DeliveryDTO> getAllDeliveries();
    public DeliveryDTO getDeliveryById(Long id);
    public DeliveryDTO updateDelivery(Long id, DeliveryDTO dto);
    public void deleteDelivery(Long id);

}
