package com.deliveryoptimizer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseDTO {
    private Long id;
    private double altitude;
    private double longitude;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
}
