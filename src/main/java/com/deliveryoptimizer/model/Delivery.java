package com.deliveryoptimizer.model;

import com.deliveryoptimizer.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double altitude;

    private double longitude;

    @Min(0)
    private double maxWeight;

    @Min(0)
    private double maxVolume;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    private String timeSlot;

    @ManyToOne
    Tour tour;
}
