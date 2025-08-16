package com.nelumbo.parking.controllers;

import com.nelumbo.parking.services.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(ReportController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReportControllerTest {

    @MockBean
    private ReportService reportService;

    @Autowired
    private ReportController reportController;

    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesAllParkings_AsAdmin_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 5));
        when(reportService.getTopVehiclesAllParkings(10)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesAllParkings(10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesAllParkings(10);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getTopVehiclesAllParkings_AsSocio_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 5));
        when(reportService.getTopVehiclesAllParkings(10)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesAllParkings(10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesAllParkings(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesByParking_AsAdmin_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 3));
        when(reportService.getTopVehiclesByParking(1L, 10)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesByParking(1L, 10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesByParking(1L, 10);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getTopVehiclesByParking_AsSocio_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 3));
        when(reportService.getTopVehiclesByParking(1L, 10)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesByParking(1L, 10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesByParking(1L, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFirstTimeVehiclesByParking_AsAdmin_Success() {
        // Arrange
        List<Map<String, Object>> firstTimeVehicles = List.of(Map.of("placa", "XYZ789", "firstTime", true));
        when(reportService.getFirstTimeVehiclesByParking(1L)).thenReturn(firstTimeVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getFirstTimeVehiclesByParking(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getFirstTimeVehiclesByParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getFirstTimeVehiclesByParking_AsSocio_Success() {
        // Arrange
        List<Map<String, Object>> firstTimeVehicles = List.of(Map.of("placa", "XYZ789", "firstTime", true));
        when(reportService.getFirstTimeVehiclesByParking(1L)).thenReturn(firstTimeVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getFirstTimeVehiclesByParking(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getFirstTimeVehiclesByParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getEarningsByPeriod_AsSocio_Success() {
        // Arrange
        Map<String, Object> earnings = Map.of("period", "today", "amount", 50000.0);
        when(reportService.getEarningsByPeriod(1L, "today")).thenReturn(earnings);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getEarningsByPeriod(1L, "today");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("today", response.getBody().get("period"));
        verify(reportService).getEarningsByPeriod(1L, "today");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEarningsByPeriod_AsAdmin_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            reportController.getEarningsByPeriod(1L, "today"));
        verify(reportService, never()).getEarningsByPeriod(anyLong(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEarningsByParkingAndDate_AsAdmin_WithDate_Success() {
        // Arrange
        Map<String, Object> earnings = Map.of("date", testDate.toString(), "amount", 75000.0);
        when(reportService.getEarningsByParkingAndDate(1L, testDate)).thenReturn(earnings);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getEarningsByParkingAndDate(1L, testDate);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testDate.toString(), response.getBody().get("date"));
        verify(reportService).getEarningsByParkingAndDate(1L, testDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEarningsByParkingAndDate_AsAdmin_WithoutDate_Success() {
        // Arrange
        Map<String, Object> earnings = Map.of("date", "today", "amount", 50000.0);
        when(reportService.getEarningsByParkingAndDate(1L, null)).thenReturn(earnings);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getEarningsByParkingAndDate(1L, null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("today", response.getBody().get("date"));
        verify(reportService).getEarningsByParkingAndDate(1L, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllParkingsEarningsByDate_AsAdmin_WithDate_Success() {
        // Arrange
        List<Map<String, Object>> allEarnings = List.of(
            Map.of("parkingId", 1L, "amount", 50000.0),
            Map.of("parkingId", 2L, "amount", 75000.0)
        );
        when(reportService.getAllParkingsEarningsByDate(testDate)).thenReturn(allEarnings);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getAllParkingsEarningsByDate(testDate);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(reportService).getAllParkingsEarningsByDate(testDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllParkingsEarningsByDate_AsAdmin_WithoutDate_Success() {
        // Arrange
        List<Map<String, Object>> allEarnings = List.of(
            Map.of("parkingId", 1L, "amount", 50000.0),
            Map.of("parkingId", 2L, "amount", 75000.0)
        );
        when(reportService.getAllParkingsEarningsByDate(null)).thenReturn(allEarnings);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getAllParkingsEarningsByDate(null);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(reportService).getAllParkingsEarningsByDate(null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGeneralStatistics_AsAdmin_Success() {
        // Arrange
        Map<String, Object> statistics = Map.of(
            "totalParkings", 5,
            "totalVehicles", 25,
            "totalEarnings", 150000.0
        );
        when(reportService.getGeneralStatistics()).thenReturn(statistics);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getGeneralStatistics();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().get("totalParkings"));
        verify(reportService).getGeneralStatistics();
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getGeneralStatistics_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            reportController.getGeneralStatistics());
        verify(reportService, never()).getGeneralStatistics();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesAllParkings_WithDefaultLimit_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 5));
        when(reportService.getTopVehiclesAllParkings(10)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesAllParkings(10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesAllParkings(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesAllParkings_WithCustomLimit_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 5));
        when(reportService.getTopVehiclesAllParkings(20)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesAllParkings(20);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesAllParkings(20);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesByParking_WithDefaultLimit_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 3));
        when(reportService.getTopVehiclesByParking(1L, 10)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesByParking(1L, 10);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesByParking(1L, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesByParking_WithCustomLimit_Success() {
        // Arrange
        List<Map<String, Object>> topVehicles = List.of(Map.of("placa", "ABC123", "count", 3));
        when(reportService.getTopVehiclesByParking(1L, 15)).thenReturn(topVehicles);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = reportController.getTopVehiclesByParking(1L, 15);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesByParking(1L, 15);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getEarningsByPeriod_WithDifferentPeriods_Success() {
        // Arrange
        Map<String, Object> earnings = Map.of("period", "today", "amount", 50000.0);
        when(reportService.getEarningsByPeriod(1L, "today")).thenReturn(earnings);

        // Act
        ResponseEntity<Map<String, Object>> response = reportController.getEarningsByPeriod(1L, "today");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("today", response.getBody().get("period"));
        verify(reportService).getEarningsByPeriod(1L, "today");
    }

    // Tests para verificar que SOCIO no puede acceder a endpoints de ADMIN
    @WithMockUser(roles = "SOCIO")
    @ParameterizedTest(name = "[{index}] SOCIO no autorizado: {0}")
    @MethodSource("forbiddenForSocioCases")
    void endpointsForbiddenForSocio_throwAccessDenied(String label,
                                                      Executable endpointCall,
                                                      ServiceCall verifyKind) {
        assertThrows(AccessDeniedException.class, endpointCall);
        switch (verifyKind) {
            case EARNINGS_BY_PARKING_AND_DATE ->
                    verify(reportService, never()).getEarningsByParkingAndDate(anyLong(), any());
            case ALL_PARKINGS_EARNINGS_BY_DATE ->
                    verify(reportService, never()).getAllParkingsEarningsByDate(any());
            case GENERAL_STATISTICS ->
                    verify(reportService, never()).getGeneralStatistics();
        }
    }

    // Method source para tests parametrizados
    Stream<Arguments> forbiddenForSocioCases() {
        LocalDate today = LocalDate.now();
        return Stream.of(
            arguments("getEarningsByParkingAndDate",
                    (Executable) () -> reportController.getEarningsByParkingAndDate(1L, today),
                    ServiceCall.EARNINGS_BY_PARKING_AND_DATE),
            arguments("getAllParkingsEarningsByDate",
                    (Executable) () -> reportController.getAllParkingsEarningsByDate(today),
                    ServiceCall.ALL_PARKINGS_EARNINGS_BY_DATE),
            arguments("getGeneralStatistics",
                    (Executable) () -> reportController.getGeneralStatistics(),
                    ServiceCall.GENERAL_STATISTICS)
        );
    }

    // Enum para identificar qué servicio verificar
    private enum ServiceCall {
        EARNINGS_BY_PARKING_AND_DATE,
        ALL_PARKINGS_EARNINGS_BY_DATE,
        GENERAL_STATISTICS
    }
}
