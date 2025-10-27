package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.TourDTO;
import com.deliveryoptimizer.model.enums.OptimizationMethod;

import java.util.List;

public interface TourService {

    TourDTO createTour(TourDTO dto);
    List<TourDTO> getAllTours();
    TourDTO getTourById(Long id);
    TourDTO updateTour(Long id, TourDTO dto);
    void deleteTour(Long id);
    TourDTO addDeliveriesToTour(Long tourId, List<Long> deliveryIds);
    List<Long> optimizeTour(Long tourId, OptimizationMethod method);

}
