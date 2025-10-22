package com.deliveryoptimizer.controller;

import com.deliveryoptimizer.dto.DeliveryDTO;
import com.deliveryoptimizer.service.impl.DeliveryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService){
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public DeliveryDTO createDelivery(@RequestBody DeliveryDTO dto){
        return deliveryService.createDelivery(dto);
    }

    @GetMapping
    public List<DeliveryDTO> getAllDeliveries(){
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/{id}")
    public DeliveryDTO getDeliveryById(@PathVariable Long id){
        return deliveryService.getDeliveryById(id);
    }

    @PutMapping("/{id}")
    public DeliveryDTO updateDelivery(@PathVariable Long id, @RequestBody DeliveryDTO dto){
        return deliveryService.updateDelivery(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteDelivery(@PathVariable Long id){
        deliveryService.deleteDelivery(id);
    }
}
