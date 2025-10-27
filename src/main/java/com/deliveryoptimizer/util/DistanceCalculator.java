package com.deliveryoptimizer.util;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Warehouse;

public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS_KM * c;
    }

    public double distance(Warehouse w, Delivery d){
        return distance(w.getAltitude(), w.getLongitude(), d.getAltitude(), d.getLongitude());
    }

    public double distance(Delivery d1, Delivery d2){
        return distance(d1.getAltitude(), d1.getLongitude(), d2.getAltitude(), d2.getLongitude());
    }
}
