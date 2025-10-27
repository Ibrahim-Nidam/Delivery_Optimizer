package com.deliveryoptimizer.util;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Warehouse;

import java.util.List;

public class TourUtils {
    private TourUtils(){}

    public static double calculateTotalDistance(Warehouse warehouse, List<Delivery> deliveries, DistanceCalculator distanceCalculator){
        if(warehouse == null || deliveries == null || deliveries.isEmpty()) return 0;

        double totalDistance = 0;
        double currentLat = warehouse.getAltitude();
        double currentLon = warehouse.getLongitude();

        for (Delivery d : deliveries){
            totalDistance += distanceCalculator.distance(currentLat, currentLon, d.getAltitude(), d.getLongitude());
            currentLat = d.getAltitude();
            currentLon = d.getLongitude();
        }

        totalDistance += distanceCalculator.distance(currentLat, currentLon, warehouse.getAltitude(), warehouse.getLongitude());

        return totalDistance;
    }

    public static String formatDistance(double distanceInKm){
        if(distanceInKm < 1){
            return String.format("%.0f m", distanceInKm * 1000);
        } else {
            return String.format("%.1f Km", distanceInKm);
        }
    }
}
