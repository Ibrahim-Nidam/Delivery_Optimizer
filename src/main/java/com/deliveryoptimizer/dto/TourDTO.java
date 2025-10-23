package com.deliveryoptimizer.dto;

import com.deliveryoptimizer.model.enums.TourStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TourDTO {
    private Long id;
    private Long vehicleId;
    private List<Long> deliveryIds;
    private Long warehouseId;
    private LocalDate date;
    private double totalDistance;
    private TourStatus status;
}
