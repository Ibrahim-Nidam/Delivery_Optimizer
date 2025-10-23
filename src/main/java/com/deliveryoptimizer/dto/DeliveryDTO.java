package com.deliveryoptimizer.dto;

import com.deliveryoptimizer.model.enums.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDTO {
    private Long id;
    private Long tourId;
    private double altitude;
    private double longitude;
    private double maxWeight;
    private double maxVolume;
    private String timeSlot;
    private DeliveryStatus status;
}
