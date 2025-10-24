package com.deliveryoptimizer.model;

import com.deliveryoptimizer.model.enums.TourStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    private double totalDistance;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private TourStatus status;

    @ManyToOne
    Vehicle vehicle;

    @OneToMany(mappedBy = "tour",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    List<Delivery> deliveries;

    @ManyToOne
    Warehouse warehouse;
}
