package com.deliveryoptimizer.model;

import com.deliveryoptimizer.model.enums.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    @JsonIgnore
    Tour tour;
}
