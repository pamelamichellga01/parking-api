package com.nelumbo.parking.services;

import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.VehicleHistory;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.ParkingRecordRepository;
import com.nelumbo.parking.repositories.ParkingRepository;
import com.nelumbo.parking.repositories.VehicleHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private VehicleHistoryRepository vehicleHistoryRepository;

    @Mock
    private ParkingRecordRepository parkingRecordRepository;

    @Mock
    private ParkingRepository parkingRepository;

    @InjectMocks
    private ReportService reportService;

    private Parking testParking;
    private VehicleHistory testHistory1;
    private VehicleHistory testHistory2;

    @BeforeEach
    void setUp() {
        testParking = Parking.builder()
                .id(1L)
                .name("Test Parking")
                .capacity(50)
                .hourlyRate(BigDecimal.valueOf(5.00))
                .build();

        testHistory1 = VehicleHistory.builder()
                .id(1L)
                .licensePlate("ABC123")
                .parkingName("Test Parking")
                .entryDateTime(LocalDateTime.now().minusHours(2))
                .exitDateTime(LocalDateTime.now())
                .totalCost(BigDecimal.valueOf(10.00))
                .parkingId(1L)
                .vehicleId(1L)
                .build();

        testHistory2 = VehicleHistory.builder()
                .id(2L)
                .licensePlate("XYZ789")
                .parkingName("Test Parking")
                .entryDateTime(LocalDateTime.now().minusHours(1))
                .exitDateTime(LocalDateTime.now())
                .totalCost(BigDecimal.valueOf(5.00))
                .parkingId(1L)
                .vehicleId(2L)
                .build();
    }

    @Test
    void getTopVehiclesByParking_Success() {
        // Arrange
        when(parkingRepository.existsById(1L)).thenReturn(true);
        when(vehicleHistoryRepository.findByParkingId(1L))
                .thenReturn(Arrays.asList(testHistory1, testHistory1, testHistory2));

        // Act
        List<Map<String, Object>> result = reportService.getTopVehiclesByParking(1L, 5);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ABC123", result.get(0).get("placa"));
        assertEquals(2L, result.get(0).get("totalRegistros"));
        assertEquals("XYZ789", result.get(1).get("placa"));
        assertEquals(1L, result.get(1).get("totalRegistros"));
    }

    @Test
    void getTopVehiclesByParking_ParkingNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            reportService.getTopVehiclesByParking(999L, 5);
        });
    }

    @Test
    void getFirstTimeVehiclesByParking_Success() {
        // Arrange
        when(parkingRepository.existsById(1L)).thenReturn(true);
        when(vehicleHistoryRepository.findByParkingId(1L))
                .thenReturn(Arrays.asList(testHistory1, testHistory2));

        // Act
        List<Map<String, Object>> result = reportService.getFirstTimeVehiclesByParking(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(v -> v.get("totalVisitas").equals(1L)));
    }

    @Test
    void getEarningsByPeriod_Today_Success() {
        // Arrange
        when(parkingRepository.existsById(1L)).thenReturn(true);
        when(vehicleHistoryRepository.findByParkingIdAndExitDateTimeBetween(anyLong(), any(), any()))
                .thenReturn(Arrays.asList(testHistory1, testHistory2));

        // Act
        Map<String, Object> result = reportService.getEarningsByPeriod(1L, "today");

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.get("parkingId"));
        assertEquals("today", result.get("periodo"));
        assertEquals(2, result.get("totalVehiculos"));
        assertEquals(BigDecimal.valueOf(15.00), result.get("gananciasTotales"));
    }

    @Test
    void getEarningsByPeriod_InvalidPeriod_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.existsById(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            reportService.getEarningsByPeriod(1L, "invalid");
        });
    }

    @Test
    void getGeneralStatistics_Success() {
        // Arrange
        when(parkingRepository.count()).thenReturn(5L);
        when(vehicleHistoryRepository.countByExitDateTimeBetween(any(), any())).thenReturn(25L);
        when(vehicleHistoryRepository.findByExitDateTimeBetween(any(), any()))
                .thenReturn(Arrays.asList(testHistory1, testHistory2));
        when(parkingRecordRepository.countActiveByParkingId(null)).thenReturn(10L);

        // Act
        Map<String, Object> result = reportService.getGeneralStatistics();

        // Assert
        assertNotNull(result);
        assertEquals(5L, result.get("totalParqueaderos"));
        assertEquals(25L, result.get("vehiculosRegistradosHoy"));
        assertEquals(BigDecimal.valueOf(15.00), result.get("gananciasHoy"));
        assertEquals(10L, result.get("vehiculosEstacionados"));
    }
}
