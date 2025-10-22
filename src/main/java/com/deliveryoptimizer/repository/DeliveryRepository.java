package com.deliveryoptimizer.repository;

import com.deliveryoptimizer.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
