package com.nelumbo.parking.controllers;

import com.nelumbo.parking.services.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // permite usar MethodSource NO estáticos
@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private Map<String, Object> testReportData;

    private enum ServiceCall {
        EARNINGS_BY_PARKING_AND_DATE,
        ALL_PARKINGS_EARNINGS_BY_DATE,
        GENERAL_STATISTICS
    }

    @BeforeEach
    void setUp() {
        testReportData = new HashMap<>();
        testReportData.put("placa", "ABC123");
        testReportData.put("totalRegistros", 5L);
        testReportData.put("ganancias", BigDecimal.valueOf(25.50));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesAllParkings_AsAdmin_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesAllParkings(10)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesAllParkings(10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC123", response.getBody().get(0).get("placa"));
        verify(reportService).getTopVehiclesAllParkings(10);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getTopVehiclesAllParkings_AsSocio_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesAllParkings(5)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesAllParkings(5);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesAllParkings(5);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesByParking_AsAdmin_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesByParking(1L, 10)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesByParking(1L, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC123", response.getBody().get(0).get("placa"));
        verify(reportService).getTopVehiclesByParking(1L, 10);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getTopVehiclesByParking_AsSocio_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesByParking(1L, 5)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesByParking(1L, 5);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getTopVehiclesByParking(1L, 5);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getFirstTimeVehiclesByParking_AsAdmin_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getFirstTimeVehiclesByParking(1L)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getFirstTimeVehiclesByParking(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC123", response.getBody().get(0).get("placa"));
        verify(reportService).getFirstTimeVehiclesByParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getFirstTimeVehiclesByParking_AsSocio_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getFirstTimeVehiclesByParking(1L)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getFirstTimeVehiclesByParking(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(reportService).getFirstTimeVehiclesByParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getEarningsByPeriod_AsSocio_Success() {
        // Arrange
        Map<String, Object> earningsData = new HashMap<>();
        earningsData.put("parkingId", 1L);
        earningsData.put("periodo", "month");
        earningsData.put("totalVehiculos", 25L);
        earningsData.put("gananciasTotales", BigDecimal.valueOf(125.75));
        
        when(reportService.getEarningsByPeriod(1L, "month")).thenReturn(earningsData);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                reportController.getEarningsByPeriod(1L, "month");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("parkingId"));
        assertEquals("month", response.getBody().get("periodo"));
        verify(reportService).getEarningsByPeriod(1L, "month");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEarningsByPeriod_AsAdmin_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> reportController.getEarningsByPeriod(1L, "month"));
        // No se debe llamar al servicio porque se lanza la excepción antes
        verifyNoInteractions(reportService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEarningsByParkingAndDate_AsAdmin_Success() {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        Map<String, Object> earningsData = new HashMap<>();
        earningsData.put("parkingId", 1L);
        earningsData.put("fecha", testDate);
        earningsData.put("totalVehiculos", 30L);
        earningsData.put("gananciasTotales", BigDecimal.valueOf(150.00));
        
        when(reportService.getEarningsByParkingAndDate(1L, testDate)).thenReturn(earningsData);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                reportController.getEarningsByParkingAndDate(1L, testDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("parkingId"));
        assertEquals(testDate, response.getBody().get("fecha"));
        verify(reportService).getEarningsByParkingAndDate(1L, testDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getEarningsByParkingAndDate_AsAdmin_WithoutDate_Success() {
        // Arrange
        Map<String, Object> earningsData = new HashMap<>();
        earningsData.put("parkingId", 1L);
        earningsData.put("totalVehiculos", 30L);
        earningsData.put("gananciasTotales", BigDecimal.valueOf(150.00));
        
        when(reportService.getEarningsByParkingAndDate(1L, null)).thenReturn(earningsData);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                reportController.getEarningsByParkingAndDate(1L, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("parkingId"));
        verify(reportService).getEarningsByParkingAndDate(1L, null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllParkingsEarningsByDate_AsAdmin_Success() {
        // Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        Map<String, Object> parking1Data = new HashMap<>();
        parking1Data.put("parkingId", 1L);
        parking1Data.put("nombre", "Parking 1");
        parking1Data.put("ganancias", BigDecimal.valueOf(75.50));
        
        Map<String, Object> parking2Data = new HashMap<>();
        parking2Data.put("parkingId", 2L);
        parking2Data.put("nombre", "Parking 2");
        parking2Data.put("ganancias", BigDecimal.valueOf(100.25));
        
        List<Map<String, Object>> expectedData = Arrays.asList(parking1Data, parking2Data);
        when(reportService.getAllParkingsEarningsByDate(testDate)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getAllParkingsEarningsByDate(testDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).get("parkingId"));
        assertEquals(2L, response.getBody().get(1).get("parkingId"));
        verify(reportService).getAllParkingsEarningsByDate(testDate);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllParkingsEarningsByDate_AsAdmin_WithoutDate_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList();
        when(reportService.getAllParkingsEarningsByDate(null)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getAllParkingsEarningsByDate(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(reportService).getAllParkingsEarningsByDate(null);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getGeneralStatistics_AsAdmin_Success() {
        // Arrange
        Map<String, Object> statisticsData = new HashMap<>();
        statisticsData.put("totalParqueaderos", 5L);
        statisticsData.put("vehiculosRegistradosHoy", 25L);
        statisticsData.put("gananciasHoy", BigDecimal.valueOf(125.50));
        statisticsData.put("vehiculosEstacionados", 10L);
        
        when(reportService.getGeneralStatistics()).thenReturn(statisticsData);

        // Act
        ResponseEntity<Map<String, Object>> response = 
                reportController.getGeneralStatistics();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().get("totalParqueaderos"));
        assertEquals(25L, response.getBody().get("vehiculosRegistradosHoy"));
        verify(reportService).getGeneralStatistics();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesAllParkings_WithDefaultLimit_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesAllParkings(10)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesAllParkings(10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).getTopVehiclesAllParkings(10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesByParking_WithDefaultLimit_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesByParking(1L, 10)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesByParking(1L, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).getTopVehiclesByParking(1L, 10);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesAllParkings_WithCustomLimit_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesAllParkings(20)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesAllParkings(20);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).getTopVehiclesAllParkings(20);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopVehiclesByParking_WithCustomLimit_Success() {
        // Arrange
        List<Map<String, Object>> expectedData = Arrays.asList(testReportData);
        when(reportService.getTopVehiclesByParking(1L, 15)).thenReturn(expectedData);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = 
                reportController.getTopVehiclesByParking(1L, 15);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reportService).getTopVehiclesByParking(1L, 15);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getEarningsByPeriod_WithDifferentPeriods_Success() {
        // Arrange
        String[] periods = {"today", "week", "month", "year"};
        
        for (String period : periods) {
            Map<String, Object> earningsData = new HashMap<>();
            earningsData.put("parkingId", 1L);
            earningsData.put("periodo", period);
            earningsData.put("totalVehiculos", 25L);
            earningsData.put("gananciasTotales", BigDecimal.valueOf(125.75));
            
            when(reportService.getEarningsByPeriod(1L, period)).thenReturn(earningsData);

            // Act
            ResponseEntity<Map<String, Object>> response = 
                    reportController.getEarningsByPeriod(1L, period);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(period, response.getBody().get("periodo"));
        }
        
        verify(reportService, times(periods.length)).getEarningsByPeriod(anyLong(), anyString());
    }

    @WithMockUser(roles = "SOCIO")
    @ParameterizedTest(name = "[{index}] SOCIO no autorizado: {0}")
    @MethodSource("forbiddenForSocioCases")
    void endpointsForbiddenForSocio_throwAccessDenied(String label,
                                                      Executable endpointCall,
                                                      ServiceCall verifyKind) {
        // lambda con UNA sola invocación (endpointCall)
        assertThrows(AccessDeniedException.class, endpointCall);

        // verifica que NO se llamó el servicio correspondiente
        switch (verifyKind) {
            case EARNINGS_BY_PARKING_AND_DATE ->
                    verify(reportService, never()).getEarningsByParkingAndDate(anyLong(), any());
            case ALL_PARKINGS_EARNINGS_BY_DATE ->
                    verify(reportService, never()).getAllParkingsEarningsByDate(any());
            case GENERAL_STATISTICS ->
                    verify(reportService, never()).getGeneralStatistics();
        }
    }

    // MethodSource NO estático (permitido por @TestInstance(PER_CLASS))
    Stream<Arguments> forbiddenForSocioCases() {
        LocalDate today = LocalDate.now(); // fuera del lambda para cumplir Sonar
        return Stream.of(
            arguments("getEarningsByParkingAndDate",
                    (Executable) () -> reportController.getEarningsByParkingAndDate(1L, today),
                    ServiceCall.EARNINGS_BY_PARKING_AND_DATE),
            arguments("getAllParkingsEarningsByDate",
                    (Executable) () -> reportController.getAllParkingsEarningsByDate(today),
                    ServiceCall.ALL_PARKINGS_EARNINGS_BY_DATE),
            arguments("getGeneralStatistics",
                    (Executable) (reportController::getGeneralStatistics),
                    ServiceCall.GENERAL_STATISTICS)
        );
    }
}
