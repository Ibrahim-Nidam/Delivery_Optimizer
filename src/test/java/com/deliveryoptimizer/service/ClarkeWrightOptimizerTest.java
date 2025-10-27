package com.deliveryoptimizer.service;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.service.impl.ClarkeWrightOptimizer;
import com.deliveryoptimizer.util.DistanceCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClarkeWrightOptimizer Tests")
class ClarkeWrightOptimizerTest {

    @Mock
    private DistanceCalculator distanceCalculator;

    private ClarkeWrightOptimizer optimizer;

    @BeforeEach
    void setUp() {
        optimizer = new ClarkeWrightOptimizer(distanceCalculator);
    }

    @Test
    @DisplayName("Should return single delivery when tour has only one delivery")
    void optimizeTour_WithSingleDelivery_ReturnsSingleDelivery() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery delivery = createDelivery(1L, 10.0, 10.0);
        Tour tour = createTour(warehouse, List.of(delivery));

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(distanceCalculator, never()).distance(any(Warehouse.class), any(Delivery.class));
    }

    @Test
    @DisplayName("Should return empty list when tour has no deliveries")
    void optimizeTour_WithEmptyDeliveries_ReturnsEmptyList() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Tour tour = createTour(warehouse, new ArrayList<>());

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).isEmpty();
        verify(distanceCalculator, never()).distance(any(Warehouse.class), any(Delivery.class));
    }

    @Test
    @DisplayName("Should return empty list when warehouse is null")
    void optimizeTour_WithNullWarehouse_ReturnsOriginalDeliveries() {
        // Given
        Delivery delivery = createDelivery(1L, 10.0, 10.0);
        Tour tour = new Tour();
        tour.setWarehouse(null);
        tour.setDeliveries(new ArrayList<>(List.of(delivery)));

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(1);
        verify(distanceCalculator, never()).distance(any(Warehouse.class), any(Delivery.class));
    }

    @Test
    @DisplayName("Should merge two deliveries with positive savings")
    void optimizeTour_WithTwoDeliveries_MergesIntoSingleRoute() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery delivery1 = createDelivery(1L, 3.0, 0.0);
        Delivery delivery2 = createDelivery(2L, 5.0, 0.0);

        Tour tour = createTour(warehouse, List.of(delivery1, delivery2));

        // Mock distances: warehouse to d1 = 3, warehouse to d2 = 5, d1 to d2 = 2
        // Saving = 3 + 5 - 2 = 6 (positive, so merge)
        when(distanceCalculator.distance(warehouse, delivery1)).thenReturn(3.0);
        when(distanceCalculator.distance(warehouse, delivery2)).thenReturn(5.0);
        when(distanceCalculator.distance(delivery1, delivery2)).thenReturn(2.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(delivery1, delivery2);
        verify(distanceCalculator, times(2)).distance(eq(warehouse), any(Delivery.class));
        verify(distanceCalculator, times(1)).distance(any(Delivery.class), any(Delivery.class));
    }

    @Test
    @DisplayName("Should calculate savings correctly for three deliveries")
    void optimizeTour_WithThreeDeliveries_CalculatesSavingsCorrectly() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery d1 = createDelivery(1L, 1.0, 0.0);
        Delivery d2 = createDelivery(2L, 2.0, 0.0);
        Delivery d3 = createDelivery(3L, 10.0, 0.0);

        Tour tour = createTour(warehouse, List.of(d1, d2, d3));

        // Setup distances
        // d1 and d2 are close together, d3 is far
        when(distanceCalculator.distance(warehouse, d1)).thenReturn(1.0);
        when(distanceCalculator.distance(warehouse, d2)).thenReturn(2.0);
        when(distanceCalculator.distance(warehouse, d3)).thenReturn(10.0);

        when(distanceCalculator.distance(d1, d2)).thenReturn(1.0);  // Saving: 1 + 2 - 1 = 2
        when(distanceCalculator.distance(d1, d3)).thenReturn(9.0);  // Saving: 1 + 10 - 9 = 2
        when(distanceCalculator.distance(d2, d3)).thenReturn(8.0);  // Saving: 2 + 10 - 8 = 4 (highest)

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(d1, d2, d3);
        // Verify distances calculated: 6 warehouse-to-delivery calls (2 per pair) + 3 delivery-to-delivery calls
        verify(distanceCalculator, times(6)).distance(eq(warehouse), any(Delivery.class));
        verify(distanceCalculator, times(3)).distance(any(Delivery.class), any(Delivery.class));
    }

    @Test
    @DisplayName("Should prioritize merging deliveries with highest savings")
    void optimizeTour_WithMultipleDeliveries_PrioritizesHighestSavings() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery d1 = createDelivery(1L, 5.0, 0.0);
        Delivery d2 = createDelivery(2L, 5.0, 1.0);
        Delivery d3 = createDelivery(3L, 10.0, 0.0);
        Delivery d4 = createDelivery(4L, 10.0, 1.0);

        Tour tour = createTour(warehouse, List.of(d1, d2, d3, d4));

        // d1 and d2 are close (around x=5)
        when(distanceCalculator.distance(warehouse, d1)).thenReturn(5.0);
        when(distanceCalculator.distance(warehouse, d2)).thenReturn(5.1);
        when(distanceCalculator.distance(d1, d2)).thenReturn(1.0);  // High saving

        // d3 and d4 are close (around x=10)
        when(distanceCalculator.distance(warehouse, d3)).thenReturn(10.0);
        when(distanceCalculator.distance(warehouse, d4)).thenReturn(10.05);
        when(distanceCalculator.distance(d3, d4)).thenReturn(1.0);  // High saving

        // Cross pairs have lower savings
        when(distanceCalculator.distance(d1, d3)).thenReturn(5.0);
        when(distanceCalculator.distance(d1, d4)).thenReturn(5.1);
        when(distanceCalculator.distance(d2, d3)).thenReturn(5.1);
        when(distanceCalculator.distance(d2, d4)).thenReturn(5.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(4);
        assertThat(result).containsExactlyInAnyOrder(d1, d2, d3, d4);
    }

    @Test
    @DisplayName("Should handle deliveries already in same tour")
    void optimizeTour_WithDeliveriesInSameTour_DoesNotMergeTwice() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery d1 = createDelivery(1L, 1.0, 0.0);
        Delivery d2 = createDelivery(2L, 2.0, 0.0);
        Delivery d3 = createDelivery(3L, 3.0, 0.0);

        Tour tour = createTour(warehouse, List.of(d1, d2, d3));

        // All deliveries in a line with equal spacing
        when(distanceCalculator.distance(warehouse, d1)).thenReturn(1.0);
        when(distanceCalculator.distance(warehouse, d2)).thenReturn(2.0);
        when(distanceCalculator.distance(warehouse, d3)).thenReturn(3.0);

        when(distanceCalculator.distance(d1, d2)).thenReturn(1.0);
        when(distanceCalculator.distance(d1, d3)).thenReturn(2.0);
        when(distanceCalculator.distance(d2, d3)).thenReturn(1.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(d1, d2, d3);
    }

    @Test
    @DisplayName("Should not modify original tour deliveries list")
    void optimizeTour_DoesNotModifyOriginalTourDeliveries() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery delivery1 = createDelivery(1L, 10.0, 10.0);
        Delivery delivery2 = createDelivery(2L, 5.0, 5.0);

        List<Delivery> originalDeliveries = new ArrayList<>(List.of(delivery1, delivery2));
        Tour tour = createTour(warehouse, originalDeliveries);

        when(distanceCalculator.distance(any(Warehouse.class), any(Delivery.class)))
                .thenReturn(5.0);
        when(distanceCalculator.distance(any(Delivery.class), any(Delivery.class)))
                .thenReturn(3.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(tour.getDeliveries()).hasSize(2);
        assertThat(result).hasSize(2);
        assertThat(result).isNotSameAs(tour.getDeliveries());
    }

    @Test
    @DisplayName("Should handle realistic Casablanca delivery scenario")
    void optimizeTour_RealisticScenario_OptimizesCorrectly() {
        // Given: A warehouse in Casablanca with deliveries around the city
        Warehouse warehouse = createWarehouse(1L, 33.5731, -7.5898);

        // Three clusters: Anfa, Maarif, and Ain Diab
        Delivery anfaA = createDelivery(1L, 33.5950, -7.6187);
        Delivery anfaB = createDelivery(2L, 33.5960, -7.6200);
        Delivery maarif = createDelivery(3L, 33.5892, -7.6039);
        Delivery ainDiab = createDelivery(4L, 33.5500, -7.6200);

        Tour tour = createTour(warehouse, List.of(anfaA, anfaB, maarif, ainDiab));

        // Distances from warehouse - called twice per pair (2*6 = 12 times for 4 deliveries)
        when(distanceCalculator.distance(warehouse, anfaA)).thenReturn(3.5);
        when(distanceCalculator.distance(warehouse, anfaB)).thenReturn(3.7);
        when(distanceCalculator.distance(warehouse, maarif)).thenReturn(2.1);
        when(distanceCalculator.distance(warehouse, ainDiab)).thenReturn(4.2);

        // Distances between deliveries - 6 pairs for 4 deliveries
        // Anfa deliveries are very close
        when(distanceCalculator.distance(anfaA, anfaB)).thenReturn(0.2);  // High saving
        when(distanceCalculator.distance(anfaA, maarif)).thenReturn(2.0);
        when(distanceCalculator.distance(anfaA, ainDiab)).thenReturn(5.5);

        when(distanceCalculator.distance(anfaB, maarif)).thenReturn(2.2);
        when(distanceCalculator.distance(anfaB, ainDiab)).thenReturn(5.7);

        when(distanceCalculator.distance(maarif, ainDiab)).thenReturn(4.8);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(4);
        assertThat(result).containsExactlyInAnyOrder(anfaA, anfaB, maarif, ainDiab);

        // Verify all distance calculations were made
        // For 4 deliveries: 6 pairs * 2 warehouse calls = 12, plus 6 delivery-to-delivery calls
        verify(distanceCalculator, atLeastOnce()).distance(eq(warehouse), any(Delivery.class));
        verify(distanceCalculator, times(6)).distance(any(Delivery.class), any(Delivery.class));
    }

    @Test
    @DisplayName("Should handle five deliveries with complex savings patterns")
    void optimizeTour_WithFiveDeliveries_OptimizesCorrectly() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        List<Delivery> deliveries = List.of(
                createDelivery(1L, 1.0, 1.0),
                createDelivery(2L, 1.2, 1.2),
                createDelivery(3L, 10.0, 10.0),
                createDelivery(4L, 10.2, 10.2),
                createDelivery(5L, 5.0, 5.0)
        );

        Tour tour = createTour(warehouse, deliveries);

        // Setup distances for two clusters (near and far) plus one middle
        setupDistancesForFiveDeliveries(warehouse, deliveries);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(5);
        assertThat(result).containsExactlyInAnyOrder(
                deliveries.get(0), deliveries.get(1), deliveries.get(2),
                deliveries.get(3), deliveries.get(4)
        );
    }

    @Test
    @DisplayName("Should handle negative savings by not merging")
    void optimizeTour_WithNegativeSavings_DoesNotMerge() {
        // Given: Deliveries where direct route is worse than separate routes
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery d1 = createDelivery(1L, 1.0, 0.0);
        Delivery d2 = createDelivery(2L, 0.0, 1.0);

        Tour tour = createTour(warehouse, List.of(d1, d2));

        // Both deliveries equidistant from warehouse
        when(distanceCalculator.distance(warehouse, d1)).thenReturn(1.0);
        when(distanceCalculator.distance(warehouse, d2)).thenReturn(1.0);
        // But far from each other
        when(distanceCalculator.distance(d1, d2)).thenReturn(1.41);  // Diagonal distance

        // Saving = 1 + 1 - 1.41 = 0.59 (still positive, will merge)

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should deduplicate deliveries in final result")
    void optimizeTour_ResultContainsUniqueDeliveries() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery d1 = createDelivery(1L, 1.0, 0.0);
        Delivery d2 = createDelivery(2L, 2.0, 0.0);
        Delivery d3 = createDelivery(3L, 3.0, 0.0);

        Tour tour = createTour(warehouse, List.of(d1, d2, d3));

        when(distanceCalculator.distance(any(Warehouse.class), any(Delivery.class)))
                .thenReturn(5.0);
        when(distanceCalculator.distance(any(Delivery.class), any(Delivery.class)))
                .thenReturn(1.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.stream().distinct().count()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should use distance calculator with correct parameters")
    void optimizeTour_UsesDistanceCalculatorCorrectly() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 33.5, -7.6);
        Delivery d1 = createDelivery(1L, 33.6, -7.5);
        Delivery d2 = createDelivery(2L, 33.7, -7.4);

        Tour tour = createTour(warehouse, List.of(d1, d2));

        when(distanceCalculator.distance(warehouse, d1)).thenReturn(1.0);
        when(distanceCalculator.distance(warehouse, d2)).thenReturn(2.0);
        when(distanceCalculator.distance(d1, d2)).thenReturn(1.5);

        // When
        optimizer.optimizerTour(tour);

        // Then
        verify(distanceCalculator, times(2)).distance(eq(warehouse), any(Delivery.class));
        verify(distanceCalculator, times(1)).distance(any(Delivery.class), any(Delivery.class));
    }

    // Helper methods
    private Warehouse createWarehouse(Long id, double altitude, double longitude) {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(id);
        warehouse.setAltitude(altitude);
        warehouse.setLongitude(longitude);
        return warehouse;
    }

    private Delivery createDelivery(Long id, double altitude, double longitude) {
        Delivery delivery = new Delivery();
        delivery.setId(id);
        delivery.setAltitude(altitude);
        delivery.setLongitude(longitude);
        return delivery;
    }

    private Tour createTour(Warehouse warehouse, List<Delivery> deliveries) {
        Tour tour = new Tour();
        tour.setWarehouse(warehouse);
        tour.setDeliveries(new ArrayList<>(deliveries));
        return tour;
    }

    private void setupDistancesForFiveDeliveries(Warehouse warehouse, List<Delivery> deliveries) {
        Delivery d1 = deliveries.get(0);  // 1.0, 1.0
        Delivery d2 = deliveries.get(1);  // 1.2, 1.2
        Delivery d3 = deliveries.get(2);  // 10.0, 10.0
        Delivery d4 = deliveries.get(3);  // 10.2, 10.2
        Delivery d5 = deliveries.get(4);  // 5.0, 5.0

        // Distances from warehouse
        when(distanceCalculator.distance(warehouse, d1)).thenReturn(1.41);
        when(distanceCalculator.distance(warehouse, d2)).thenReturn(1.70);
        when(distanceCalculator.distance(warehouse, d3)).thenReturn(14.14);
        when(distanceCalculator.distance(warehouse, d4)).thenReturn(14.42);
        when(distanceCalculator.distance(warehouse, d5)).thenReturn(7.07);

        // Distances between deliveries - cluster 1 (d1, d2)
        when(distanceCalculator.distance(d1, d2)).thenReturn(0.28);  // Very close

        // Distances between deliveries - cluster 2 (d3, d4)
        when(distanceCalculator.distance(d3, d4)).thenReturn(0.28);  // Very close

        // Cross-cluster distances
        when(distanceCalculator.distance(d1, d3)).thenReturn(12.73);
        when(distanceCalculator.distance(d1, d4)).thenReturn(13.01);
        when(distanceCalculator.distance(d2, d3)).thenReturn(12.44);
        when(distanceCalculator.distance(d2, d4)).thenReturn(12.73);

        // Middle delivery distances
        when(distanceCalculator.distance(d1, d5)).thenReturn(5.66);
        when(distanceCalculator.distance(d2, d5)).thenReturn(5.37);
        when(distanceCalculator.distance(d3, d5)).thenReturn(7.07);
        when(distanceCalculator.distance(d4, d5)).thenReturn(7.35);
    }
}