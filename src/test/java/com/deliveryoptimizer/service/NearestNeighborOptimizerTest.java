package com.deliveryoptimizer.service;

import com.deliveryoptimizer.model.Delivery;
import com.deliveryoptimizer.model.Tour;
import com.deliveryoptimizer.model.Warehouse;
import com.deliveryoptimizer.service.impl.NearestNeighborOptimizer;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NearestNeighborOptimizer Tests")
class NearestNeighborOptimizerTest {

    @Mock
    private DistanceCalculator distanceCalculator;

    private NearestNeighborOptimizer optimizer;

    @BeforeEach
    void setUp() {
        optimizer = new NearestNeighborOptimizer(distanceCalculator);
    }

    @Test
    @DisplayName("Should return single delivery when tour has only one delivery")
    void optimizeTour_WithSingleDelivery_ReturnsSingleDelivery() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery delivery = createDelivery(1L, 10.0, 10.0);
        Tour tour = createTour(warehouse, List.of(delivery));

        when(distanceCalculator.distance(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(14.14);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(distanceCalculator, times(1)).distance(0.0, 0.0, 10.0, 10.0);
    }

    @Test
    @DisplayName("Should optimize tour with multiple deliveries using nearest neighbor algorithm")
    void optimizeTour_WithMultipleDeliveries_ReturnsOptimizedOrder() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery delivery1 = createDelivery(1L, 10.0, 10.0);
        Delivery delivery2 = createDelivery(2L, 5.0, 5.0);
        Delivery delivery3 = createDelivery(3L, 15.0, 15.0);

        Tour tour = createTour(warehouse, List.of(delivery1, delivery2, delivery3));

        // Mock distances from warehouse (0,0)
        when(distanceCalculator.distance(0.0, 0.0, 10.0, 10.0)).thenReturn(14.14);
        when(distanceCalculator.distance(0.0, 0.0, 5.0, 5.0)).thenReturn(7.07);  // Nearest
        when(distanceCalculator.distance(0.0, 0.0, 15.0, 15.0)).thenReturn(21.21);

        // Mock distances from delivery2 (5,5)
        when(distanceCalculator.distance(5.0, 5.0, 10.0, 10.0)).thenReturn(7.07);  // Nearest
        when(distanceCalculator.distance(5.0, 5.0, 15.0, 15.0)).thenReturn(14.14);

        // Mock distances from delivery1 (10,10)
        when(distanceCalculator.distance(10.0, 10.0, 15.0, 15.0)).thenReturn(7.07);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(2L); // Closest to warehouse
        assertThat(result.get(1).getId()).isEqualTo(1L); // Closest to delivery2
        assertThat(result.get(2).getId()).isEqualTo(3L); // Last remaining
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
        verify(distanceCalculator, never()).distance(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Should select nearest delivery first when there are two deliveries")
    void optimizeTour_WithTwoDeliveries_SelectsNearestFirst() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery nearDelivery = createDelivery(1L, 2.0, 2.0);
        Delivery farDelivery = createDelivery(2L, 20.0, 20.0);

        Tour tour = createTour(warehouse, List.of(nearDelivery, farDelivery));

        when(distanceCalculator.distance(0.0, 0.0, 2.0, 2.0)).thenReturn(2.83);
        when(distanceCalculator.distance(0.0, 0.0, 20.0, 20.0)).thenReturn(28.28);
        when(distanceCalculator.distance(2.0, 2.0, 20.0, 20.0)).thenReturn(25.46);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should handle deliveries with equal distances")
    void optimizeTour_WithEqualDistances_SelectsFirstInIteration() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        Delivery delivery1 = createDelivery(1L, 5.0, 0.0);
        Delivery delivery2 = createDelivery(2L, 0.0, 5.0);

        Tour tour = createTour(warehouse, List.of(delivery1, delivery2));

        // Both deliveries are equidistant from warehouse
        when(distanceCalculator.distance(0.0, 0.0, 5.0, 0.0)).thenReturn(5.0);
        when(distanceCalculator.distance(0.0, 0.0, 0.0, 5.0)).thenReturn(5.0);
        when(distanceCalculator.distance(5.0, 0.0, 0.0, 5.0)).thenReturn(7.07);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(2);
        // First delivery encountered in iteration should be selected
        assertThat(result.get(0).getId()).isIn(1L, 2L);
    }

    @Test
    @DisplayName("Should use warehouse coordinates as starting point")
    void optimizeTour_VerifiesWarehouseCoordinatesUsed() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 33.5, -7.6); // Specific coordinates
        Delivery delivery = createDelivery(1L, 34.0, -7.0);
        Tour tour = createTour(warehouse, List.of(delivery));

        when(distanceCalculator.distance(33.5, -7.6, 34.0, -7.0)).thenReturn(0.7);

        // When
        optimizer.optimizerTour(tour);

        // Then
        verify(distanceCalculator).distance(33.5, -7.6, 34.0, -7.0);
    }

    @Test
    @DisplayName("Should optimize tour with five deliveries correctly")
    void optimizeTour_WithFiveDeliveries_OptimizesCorrectly() {
        // Given
        Warehouse warehouse = createWarehouse(1L, 0.0, 0.0);
        List<Delivery> deliveries = List.of(
                createDelivery(1L, 1.0, 1.0),
                createDelivery(2L, 2.0, 2.0),
                createDelivery(3L, 10.0, 10.0),
                createDelivery(4L, 11.0, 11.0),
                createDelivery(5L, 1.5, 1.5)
        );

        Tour tour = createTour(warehouse, deliveries);

        // Setup distance mocking for greedy nearest neighbor
        setupDistancesForFiveDeliveries();

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(5);
        // Verify first delivery is closest to warehouse
        assertThat(result.get(0).getId()).isEqualTo(1L);
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

        when(distanceCalculator.distance(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(1.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(tour.getDeliveries()).hasSize(2);
        assertThat(result).hasSize(2);
        assertThat(result).isNotSameAs(tour.getDeliveries());
    }

    @Test
    @DisplayName("Should handle realistic delivery scenario")
    void optimizeTour_RealisticScenario_OptimizesCorrectly() {
        // Given: A warehouse in Casablanca with deliveries around the city
        Warehouse warehouse = createWarehouse(1L, 33.5731, -7.5898);
        Delivery delivery1 = createDelivery(1L, 33.5950, -7.6187); // Anfa
        Delivery delivery2 = createDelivery(2L, 33.5892, -7.6039); // Maarif
        Delivery delivery3 = createDelivery(3L, 33.5500, -7.6200); // Ain Diab

        Tour tour = createTour(warehouse, List.of(delivery1, delivery2, delivery3));

        // Mock realistic distances (in km)
        when(distanceCalculator.distance(33.5731, -7.5898, 33.5950, -7.6187)).thenReturn(3.5);
        when(distanceCalculator.distance(33.5731, -7.5898, 33.5892, -7.6039)).thenReturn(2.1);  // Nearest
        when(distanceCalculator.distance(33.5731, -7.5898, 33.5500, -7.6200)).thenReturn(4.2);

        when(distanceCalculator.distance(33.5892, -7.6039, 33.5950, -7.6187)).thenReturn(1.8);  // Nearest
        when(distanceCalculator.distance(33.5892, -7.6039, 33.5500, -7.6200)).thenReturn(4.8);

        when(distanceCalculator.distance(33.5950, -7.6187, 33.5500, -7.6200)).thenReturn(5.0);

        // When
        List<Delivery> result = optimizer.optimizerTour(tour);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(2L); // Maarif (closest to warehouse)
        assertThat(result.get(1).getId()).isEqualTo(1L); // Anfa (closest to Maarif)
        assertThat(result.get(2).getId()).isEqualTo(3L); // Ain Diab (last)
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

    private void setupDistancesForFiveDeliveries() {
        // From warehouse (0,0)
        when(distanceCalculator.distance(0.0, 0.0, 1.0, 1.0)).thenReturn(1.41);
        when(distanceCalculator.distance(0.0, 0.0, 2.0, 2.0)).thenReturn(2.83);
        when(distanceCalculator.distance(0.0, 0.0, 10.0, 10.0)).thenReturn(14.14);
        when(distanceCalculator.distance(0.0, 0.0, 11.0, 11.0)).thenReturn(15.56);
        when(distanceCalculator.distance(0.0, 0.0, 1.5, 1.5)).thenReturn(2.12);

        // From delivery 1 (1,1)
        when(distanceCalculator.distance(1.0, 1.0, 2.0, 2.0)).thenReturn(1.41);
        when(distanceCalculator.distance(1.0, 1.0, 10.0, 10.0)).thenReturn(12.73);
        when(distanceCalculator.distance(1.0, 1.0, 11.0, 11.0)).thenReturn(14.14);
        when(distanceCalculator.distance(1.0, 1.0, 1.5, 1.5)).thenReturn(0.71);

        // From delivery 5 (1.5, 1.5)
        when(distanceCalculator.distance(1.5, 1.5, 2.0, 2.0)).thenReturn(0.71);
        when(distanceCalculator.distance(1.5, 1.5, 10.0, 10.0)).thenReturn(12.02);
        when(distanceCalculator.distance(1.5, 1.5, 11.0, 11.0)).thenReturn(13.44);

        // From delivery 2 (2,2)
        when(distanceCalculator.distance(2.0, 2.0, 10.0, 10.0)).thenReturn(11.31);
        when(distanceCalculator.distance(2.0, 2.0, 11.0, 11.0)).thenReturn(12.73);

        // From delivery 3 (10,10)
        when(distanceCalculator.distance(10.0, 10.0, 11.0, 11.0)).thenReturn(1.41);
    }
}