package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.ParkingRequest;
import com.nelumbo.parking.dto.AssociatePartnerRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.services.ParkingService;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingControllerTest {

    @Mock
    private ParkingService parkingService;

    @InjectMocks
    private ParkingController parkingController;

    private Parking testParking;
    private ParkingRequest parkingRequest;
    private AssociatePartnerRequest associateRequest;

    @BeforeEach
    void setUp() {
        User testPartner = User.builder()
                .id(1L)
                .name("Test Partner")
                .email("partner@test.com")
                .password("password")
                .role(Role.SOCIO)
                .build();

        testParking = Parking.builder()
                .id(1L)
                .name("Test Parking")
                .capacity(50)
                .hourlyRate(BigDecimal.valueOf(5.00))
                .partner(testPartner)
                .build();

        parkingRequest = new ParkingRequest();
        parkingRequest.setName("New Parking");
        parkingRequest.setCapacity(30);
        parkingRequest.setHourlyRate(BigDecimal.valueOf(3.00));
        parkingRequest.setPartnerId(1L);

        associateRequest = new AssociatePartnerRequest();
        associateRequest.setPartnerId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createParking_AsAdmin_Success() {
        // Arrange
        when(parkingService.createParking(any(ParkingRequest.class))).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.createParking(parkingRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Parking", response.getBody().getName());
        verify(parkingService).createParking(parkingRequest);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void createParking_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            parkingController.createParking(parkingRequest));
        verify(parkingService, never()).createParking(any(ParkingRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingById_AsAdmin_Success() {
        // Arrange
        when(parkingService.getParkingById(1L)).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.getParkingById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Parking", response.getBody().getName());
        verify(parkingService).getParkingById(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkingById_AsSocio_Success() {
        // Arrange
        when(parkingService.getParkingById(1L)).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.getParkingById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(parkingService).getParkingById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllParkings_AsAdmin_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingService.getAllParkings()).thenReturn(parkings);

        // Act
        ResponseEntity<List<Parking>> response = parkingController.getAllParkings();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(parkingService).getAllParkings();
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getAllParkings_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            parkingController.getAllParkings());
        verify(parkingService, never()).getAllParkings();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateParking_AsAdmin_Success() {
        // Arrange
        when(parkingService.updateParking(anyLong(), any(ParkingRequest.class))).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.updateParking(1L, parkingRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(parkingService).updateParking(1L, parkingRequest);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void updateParking_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            parkingController.updateParking(1L, parkingRequest));
        verify(parkingService, never()).updateParking(anyLong(), any(ParkingRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteParking_AsAdmin_Success() {
        // Arrange
        doNothing().when(parkingService).deleteParking(1L);

        // Act
        ResponseEntity<Void> response = parkingController.deleteParking(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(parkingService).deleteParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void deleteParking_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            parkingController.deleteParking(1L));
        verify(parkingService, never()).deleteParking(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingsByPartner_AsAdmin_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingService.getParkingsByPartner(1L)).thenReturn(parkings);

        // Act
        ResponseEntity<List<Parking>> response = parkingController.getParkingsByPartner(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(parkingService).getParkingsByPartner(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkingsByPartner_AsSocio_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingService.getParkingsByPartner(1L)).thenReturn(parkings);

        // Act
        ResponseEntity<List<Parking>> response = parkingController.getParkingsByPartner(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(parkingService).getParkingsByPartner(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingsByPartnerEmail_AsAdmin_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingService.getParkingsByPartnerEmail("partner@test.com")).thenReturn(parkings);

        // Act
        ResponseEntity<List<Parking>> response = parkingController.getParkingsByPartnerEmail("partner@test.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(parkingService).getParkingsByPartnerEmail("partner@test.com");
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkingsByPartnerEmail_AsSocio_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingService.getParkingsByPartnerEmail("partner@test.com")).thenReturn(parkings);

        // Act
        ResponseEntity<List<Parking>> response = parkingController.getParkingsByPartnerEmail("partner@test.com");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(parkingService).getParkingsByPartnerEmail("partner@test.com");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void associatePartnerToParking_AsAdmin_Success() {
        // Arrange
        when(parkingService.associatePartnerToParking(anyLong(), anyLong())).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.associatePartnerToParking(1L, associateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(parkingService).associatePartnerToParking(1L, 1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void associatePartnerToParking_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            parkingController.associatePartnerToParking(1L, associateRequest));
        verify(parkingService, never()).associatePartnerToParking(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removePartnerFromParking_AsAdmin_Success() {
        // Arrange
        when(parkingService.removePartnerFromParking(1L)).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.removePartnerFromParking(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(parkingService).removePartnerFromParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void removePartnerFromParking_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () ->
            parkingController.removePartnerFromParking(1L));
        verify(parkingService, never()).removePartnerFromParking(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void hasPartner_AsAdmin_Success() {
        // Arrange
        when(parkingService.hasPartner(1L)).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = parkingController.hasPartner(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());  // compara wrappers, sin unboxing
        verify(parkingService).hasPartner(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void hasPartner_AsSocio_Success() {
        // Arrange
        when(parkingService.hasPartner(1L)).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = parkingController.hasPartner(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody());
        verify(parkingService).hasPartner(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingsWithoutPartner_AsAdmin_Success() {
        // Arrange
        List<Parking> parkings = Arrays.asList(testParking);
        when(parkingService.getParkingsWithoutPartner()).thenReturn(parkings);

        // Act
        ResponseEntity<List<Parking>> response = parkingController.getParkingsWithoutPartner();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(parkingService).getParkingsWithoutPartner();
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void getParkingsWithoutPartner_AsSocio_ThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            parkingController.getParkingsWithoutPartner();
        });
        verify(parkingService, never()).getParkingsWithoutPartner();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createParking_ValidationException_ReturnsBadRequest() {
        // Arrange
        when(parkingService.createParking(any(ParkingRequest.class)))
                .thenThrow(new ValidationException("Validation error"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            parkingController.createParking(parkingRequest);
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingById_NotFound_ThrowsValidationException() {
        // Arrange
        when(parkingService.getParkingById(999L))
                .thenThrow(new ValidationException("Parqueadero no encontrado"));

        // Act & Assert
        assertThrows(ValidationException.class, () -> {
            parkingController.getParkingById(999L);
        });
    }
}
