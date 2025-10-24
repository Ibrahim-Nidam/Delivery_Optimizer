package com.deliveryoptimizer.service.interfaces;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;

import java.util.List;

public interface TourOptimizer {
    List<Delivery> optimizerTour(Tour tour);
}
