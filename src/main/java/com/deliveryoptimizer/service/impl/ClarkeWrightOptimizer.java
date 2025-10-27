package com.deliveryoptimizer.service.impl;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.service.interfaces.TourOptimizer;
import com.deliveryoptimizer.util.DistanceCalculator;

import java.util.*;

public class ClarkeWrightOptimizer implements TourOptimizer {
    private final DistanceCalculator distanceCalculator;

    public ClarkeWrightOptimizer(DistanceCalculator distanceCalculator){
        this.distanceCalculator = distanceCalculator;
    }

    @Override
    public List<Delivery> optimizerTour(Tour tour){
        Warehouse warehouse = tour.getWarehouse();
        List<Delivery> deliveries = new ArrayList<>(tour.getDeliveries());

        if(warehouse == null || deliveries.isEmpty()) return deliveries;

        Map<Delivery, List<Delivery>> tours = new HashMap<>();

        for (Delivery d : deliveries){
            tours.put(d, new ArrayList<>(List.of(d)));
        }

        List<Saving> savings = new ArrayList<>();
        for (int i = 0; i < deliveries.size(); i++){
            for (int j = i + 1; j < deliveries.size(); j++){
                Delivery a = deliveries.get(i);
                Delivery b = deliveries.get(j);
                double s = distanceCalculator.distance(warehouse, a)
                        + distanceCalculator.distance(warehouse, b)
                        - distanceCalculator.distance(a, b);
                savings.add(new Saving(a, b, s));
            }
        }

        savings.sort((s1, s2) -> Double.compare(s2.value, s1.value));

        for (Saving s : savings){
            List<Delivery> tourA = tours.get(s.d1);
            List<Delivery> tourB = tours.get(s.d2);

            if(tourB == null || tourA == null || tourA == tourB) continue;

            tourA.addAll(tourB);

            for (Delivery d : tourB){
                tours.put(d, tourA);
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(tours.values().iterator().next()));
    }

    private static class Saving{
        Delivery d1;
        Delivery d2;
        double value;

        public Saving(Delivery d1, Delivery d2, double value){
            this.d1 = d1;
            this.d2 = d2;
            this.value = value;
        }
    }
}
