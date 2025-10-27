package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.service.interfaces.TourOptimizer;
import com.deliveryoptimizer.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.List;

public class NearestNeighborOptimizer implements TourOptimizer {
    private final DistanceCalculator distanceCalculator;

    public NearestNeighborOptimizer(DistanceCalculator distanceCalculator){
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public List<Delivery> optimizerTour(Tour tour){
        List<Delivery> remaining = new ArrayList<>(tour.getDeliveries());
        List<Delivery> ordered = new ArrayList<>();

        double currentLat = tour.getWarehouse().getAltitude();
        double currentLon = tour.getWarehouse().getLongitude();

        while (!remaining.isEmpty()){
            Delivery nearest = null;
            double minDistance = Double.MAX_VALUE;

            for(Delivery d : remaining){
                double dist = distanceCalculator.distance(currentLat, currentLon, d.getAltitude(), d.getLongitude());
                if(dist < minDistance){
                    minDistance = dist;
                    nearest = d;
                }
            }

            if(nearest == null) break;

            ordered.add(nearest);
            remaining.remove(nearest);
            currentLat = nearest.getAltitude();
            currentLon = nearest.getLongitude();

        }
        return ordered;
    }
}
