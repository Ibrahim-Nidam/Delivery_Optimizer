package com.deliveryoptimizer.dto;

import com.deliveryoptimizer.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {
    private Long id;
    private String name;
    private VehicleType type;
    private double maxWeight;
    private double maxVolume;
    private int maxDeliveries;
}
