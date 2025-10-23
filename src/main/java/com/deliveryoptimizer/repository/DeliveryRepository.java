package com.deliveryoptimizer.repository;

import com.deliveryoptimizer.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    public List<Delivery> findAllByTourIsNull();
}
