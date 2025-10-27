package com.deliveryoptimizer.util;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Warehouse;

public class DistanceCalculator {
    public double distance(double lat1, double lon1, double lat2, double lon2) {
        double dx = lat1 - lat2;
        double dy = lon1 - lon2;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distance(Warehouse w, Delivery d){
        return distance(w.getAltitude(), w.getLongitude(), d.getAltitude(), d.getLongitude());
    }

    public double distance(Delivery d1, Delivery d2){
        return distance(d1.getAltitude(), d1.getLongitude(), d2.getAltitude(), d2.getLongitude());
    }
}
