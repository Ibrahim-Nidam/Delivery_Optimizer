package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.dto.TourDTO;

import java.util.List;

public interface TourService {

    public TourDTO createTour(TourDTO dto);
    public List<TourDTO> getAllTours();
    public TourDTO getTourById(Long id);
    public TourDTO updateTour(Long id, TourDTO dto);
    public void deleteTour(Long id);
    public TourDTO addDeliveriesToTour(Long tourId, List<Long> deliveryIds);

}
