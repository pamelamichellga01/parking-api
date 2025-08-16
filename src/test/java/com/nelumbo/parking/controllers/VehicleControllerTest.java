package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.VehicleEntryRequest;
import com.nelumbo.parking.dto.VehicleExitRequest;
import com.nelumbo.parking.entities.ParkingRecord;
import com.nelumbo.parking.entities.Vehicle;
import com.nelumbo.parking.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import jakarta.validation.Valid;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private VehicleController vehicleController;

    private VehicleEntryRequest entryRequest;
    private VehicleExitRequest exitRequest;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        entryRequest = new VehicleEntryRequest();
        entryRequest.setLicensePlate("ABC123");
        entryRequest.setParkingId(1L);

        exitRequest = new VehicleExitRequest();
        exitRequest.setLicensePlate("ABC123");
        exitRequest.setParkingId(1L);

        testVehicle = Vehicle.builder()
                .id(1L)
                .licensePlate("ABC123")
                .build();
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleEntry_AsSocio_Success() {
        // Arrange
        when(vehicleService.registerVehicleEntry(any(VehicleEntryRequest.class))).thenReturn(1L);

        // Act
        ResponseEntity<VehicleController.VehicleEntryResponse> response = vehicleController.registerVehicleEntry(entryRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(vehicleService).registerVehicleEntry(entryRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerVehicleEntry_AsAdmin_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            vehicleController.registerVehicleEntry(entryRequest));
        verify(vehicleService, never()).registerVehicleEntry(any(VehicleEntryRequest.class));
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleExit_AsSocio_Success() {
        // Arrange
        when(vehicleService.registerVehicleExit(any(VehicleExitRequest.class))).thenReturn("Salida registrada");

        // Act
        ResponseEntity<VehicleController.VehicleExitResponse> response = vehicleController.registerVehicleExit(exitRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Salida registrada", response.getBody().getMensaje());
        verify(vehicleService).registerVehicleExit(exitRequest);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerVehicleExit_AsAdmin_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            vehicleController.registerVehicleExit(exitRequest));
        verify(vehicleService, never()).registerVehicleExit(any(VehicleExitRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkedVehicles_AsAdmin_Success() {
        // Arrange
        List<ParkingRecord> parkedVehicles = List.of();
        when(vehicleService.getParkedVehicles(1L)).thenReturn(parkedVehicles);

        // Act
        ResponseEntity<List<ParkingRecord>> response = vehicleController.getParkedVehicles(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(vehicleService).getParkedVehicles(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkedVehicles_AsSocio_Success() {
        // Arrange
        List<ParkingRecord> parkedVehicles = List.of();
        when(vehicleService.getParkedVehicles(1L)).thenReturn(parkedVehicles);

        // Act
        ResponseEntity<List<ParkingRecord>> response = vehicleController.getParkedVehicles(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(vehicleService).getParkedVehicles(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void searchVehiclesByPlate_AsAdmin_Success() {
        // Arrange
        List<Vehicle> vehicles = List.of(testVehicle);
        when(vehicleService.searchVehiclesByPlate("ABC")).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.searchVehiclesByPlate("ABC");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(vehicleService).searchVehiclesByPlate("ABC");
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void searchVehiclesByPlate_AsSocio_Success() {
        // Arrange
        List<Vehicle> vehicles = List.of(testVehicle);
        when(vehicleService.searchVehiclesByPlate("ABC")).thenReturn(vehicles);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.searchVehiclesByPlate("ABC");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(vehicleService).searchVehiclesByPlate("ABC");
    }
}
