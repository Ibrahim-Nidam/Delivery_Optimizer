package com.deliveryoptimizer.model;

import com.deliveryoptimizer.model.enums.VehicleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=3, max=20)
    private String name;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

    @Min(0)
    private double maxWeight;

    @Min(0)
    private double maxVolume;

    @Min(1)
    private int maxDeliveries;

}
