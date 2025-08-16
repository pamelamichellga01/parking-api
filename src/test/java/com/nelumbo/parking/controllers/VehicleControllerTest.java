package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.VehicleEntryRequest;
import com.nelumbo.parking.dto.VehicleExitRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.ParkingRecord;
import com.nelumbo.parking.entities.Vehicle;
import com.nelumbo.parking.entities.ParkingRecord.ParkingStatus;
import com.nelumbo.parking.services.VehicleService;
import com.nelumbo.parking.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("FieldCanBeLocal")
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private Parking testParking;
    private Vehicle testVehicle;
    private ParkingRecord testParkingRecord;
    private VehicleEntryRequest entryRequest;
    private VehicleExitRequest exitRequest;

    @BeforeEach
    void setUp() {
        testParking = Parking.builder()
                .id(1L)
                .name("Test Parking")
                .capacity(50)
                .hourlyRate(BigDecimal.valueOf(5.00))
                .build();

        testVehicle = Vehicle.builder()
                .id(1L)
                .licensePlate("ABC123")
                .build();

        testParkingRecord = ParkingRecord.builder()
                .id(1L)
                .vehicle(testVehicle)
                .parking(testParking)
                .entryDateTime(LocalDateTime.now().minusHours(2))
                .status(ParkingStatus.PARKED)
                .build();

        entryRequest = new VehicleEntryRequest();
        entryRequest.setLicensePlate("ABC123");
        entryRequest.setParkingId(1L);

        exitRequest = new VehicleExitRequest();
        exitRequest.setLicensePlate("ABC123");
        exitRequest.setParkingId(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleEntry_AsSocio_Success() {
        // Arrange
        when(vehicleService.registerVehicleEntry(any(VehicleEntryRequest.class))).thenReturn(1L);

        // Act
        ResponseEntity<VehicleController.VehicleEntryResponse> response = 
                vehicleController.registerVehicleEntry(entryRequest);

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
        assertThrows(AccessDeniedException.class, () ->
            vehicleController.registerVehicleEntry(entryRequest));
        verify(vehicleService, never()).registerVehicleEntry(any(VehicleEntryRequest.class));
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleExit_AsSocio_Success() {
        // Arrange
        when(vehicleService.registerVehicleExit(any(VehicleExitRequest.class)))
                .thenReturn("Salida registrada");

        // Act
        ResponseEntity<VehicleController.VehicleExitResponse> response = 
                vehicleController.registerVehicleExit(exitRequest);

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
        assertThrows(AccessDeniedException.class, () ->
            vehicleController.registerVehicleExit(exitRequest));
        verify(vehicleService, never()).registerVehicleExit(any(VehicleExitRequest.class));
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkedVehicles_AsSocio_Success() {
        // Arrange
        List<ParkingRecord> parkedVehicles = List.of(testParkingRecord);
        when(vehicleService.getParkedVehicles(1L)).thenReturn(parkedVehicles);

        // Act
        ResponseEntity<List<ParkingRecord>> response = vehicleController.getParkedVehicles(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("ABC123", response.getBody().getFirst().getVehicle().getLicensePlate());
        verify(vehicleService).getParkedVehicles(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkedVehicles_AsAdmin_Success() {
        // Arrange
        List<ParkingRecord> parkedVehicles = List.of(testParkingRecord);
        when(vehicleService.getParkedVehicles(1L)).thenReturn(parkedVehicles);

        // Act
        ResponseEntity<List<ParkingRecord>> response = vehicleController.getParkedVehicles(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(vehicleService).getParkedVehicles(1L);
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
        assertEquals("ABC123", response.getBody().getFirst().getLicensePlate());
        verify(vehicleService).searchVehiclesByPlate("ABC");
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
    void registerVehicleEntry_ValidationException_ThrowsException() {
        // Arrange
        when(vehicleService.registerVehicleEntry(any(VehicleEntryRequest.class)))
                .thenThrow(new ValidationException("Validation error"));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            vehicleController.registerVehicleEntry(entryRequest));
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleExit_ValidationException_ThrowsException() {
        // Arrange
        when(vehicleService.registerVehicleExit(any(VehicleExitRequest.class)))
                .thenThrow(new ValidationException("Validation error"));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            vehicleController.registerVehicleExit(exitRequest));
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkedVehicles_ValidationException_ThrowsException() {
        // Arrange
        when(vehicleService.getParkedVehicles(1L))
                .thenThrow(new ValidationException("Parqueadero no encontrado"));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            vehicleController.getParkedVehicles(1L));
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void searchVehiclesByPlate_ValidationException_ThrowsException() {
        // Arrange
        when(vehicleService.searchVehiclesByPlate(""))
                .thenThrow(new ValidationException("La placa parcial no puede estar vacía"));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            vehicleController.searchVehiclesByPlate(""));
    }

    @Test
    void VehicleEntryResponse_ConstructorAndGetters_Success() {
        // Arrange
        Long id = 1L;

        // Act
        VehicleController.VehicleEntryResponse response = 
                new VehicleController.VehicleEntryResponse(id);

        // Assert
        assertEquals(id, response.getId());
    }

    @Test
    void VehicleEntryResponse_SettersAndGetters_Success() {
        // Arrange
        VehicleController.VehicleEntryResponse response = 
                new VehicleController.VehicleEntryResponse(1L);
        Long newId = 2L;

        // Act
        response.setId(newId);

        // Assert
        assertEquals(newId, response.getId());
    }

    @Test
    void VehicleExitResponse_ConstructorAndGetters_Success() {
        // Arrange
        String mensaje = "Test message";

        // Act
        VehicleController.VehicleExitResponse response = 
                new VehicleController.VehicleExitResponse(mensaje);

        // Assert
        assertEquals(mensaje, response.getMensaje());
    }

    @Test
    void VehicleExitResponse_SettersAndGetters_Success() {
        // Arrange
        VehicleController.VehicleExitResponse response = 
                new VehicleController.VehicleExitResponse("Original message");
        String newMensaje = "New message";

        // Act
        response.setMensaje(newMensaje);

        // Assert
        assertEquals(newMensaje, response.getMensaje());
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleEntry_WithDifferentLicensePlate_Success() {
        // Arrange
        entryRequest.setLicensePlate("XYZ789");
        when(vehicleService.registerVehicleEntry(any(VehicleEntryRequest.class))).thenReturn(2L);

        // Act
        ResponseEntity<VehicleController.VehicleEntryResponse> response = 
                vehicleController.registerVehicleEntry(entryRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        verify(vehicleService).registerVehicleEntry(entryRequest);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void registerVehicleExit_WithDifferentLicensePlate_Success() {
        // Arrange
        exitRequest.setLicensePlate("XYZ789");
        when(vehicleService.registerVehicleExit(any(VehicleExitRequest.class)))
                .thenReturn("Salida registrada para XYZ789");

        // Act
        ResponseEntity<VehicleController.VehicleExitResponse> response = 
                vehicleController.registerVehicleExit(exitRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Salida registrada para XYZ789", response.getBody().getMensaje());
        verify(vehicleService).registerVehicleExit(exitRequest);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkedVehicles_EmptyList_Success() {
        // Arrange
        List<ParkingRecord> emptyList = List.of();
        when(vehicleService.getParkedVehicles(1L)).thenReturn(emptyList);

        // Act
        ResponseEntity<List<ParkingRecord>> response = vehicleController.getParkedVehicles(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(vehicleService).getParkedVehicles(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void searchVehiclesByPlate_EmptyList_Success() {
        // Arrange
        List<Vehicle> emptyList = List.of();
        when(vehicleService.searchVehiclesByPlate("NONEXISTENT")).thenReturn(emptyList);

        // Act
        ResponseEntity<List<Vehicle>> response = vehicleController.searchVehiclesByPlate("NONEXISTENT");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
        verify(vehicleService).searchVehiclesByPlate("NONEXISTENT");
    }
}
