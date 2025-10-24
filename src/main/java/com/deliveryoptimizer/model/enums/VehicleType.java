package com.deliveryoptimizer.model.enums;

import lombok.Getter;

@Getter
public enum VehicleType {
    BIKE(50, 0.5, 15),
    VAN(1000, 8, 50),
    TRUCK(5000, 40, 100);

    private final double maxWeightKg;
    private final double maxVolumeM3;
    private final int maxDeliveries;

    VehicleType(double maxWeightKg, double maxVolumeM3, int maxDeliveries){
        this.maxWeightKg = maxWeightKg;
        this.maxVolumeM3 = maxVolumeM3;
        this.maxDeliveries = maxDeliveries;
    }
}
