package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.ParkingRequest;
import com.nelumbo.parking.dto.AssociatePartnerRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.services.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@WebMvcTest(ParkingController.class)
class ParkingControllerTest {

    @MockitoBean
    private ParkingService parkingService;

    @Autowired
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
    void createParkingAsAdminSuccess() {
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
    void createParkingAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.createParking(parkingRequest));
        verify(parkingService, never()).createParking(any(ParkingRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingByIdAsAdminSuccess() {
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
    void getParkingByIdAsSocioSuccess() {
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
    @WithMockUser(roles = "ADMIN")
    void getAllParkingsAsAdminSuccess() {
        // Arrange
        List<Parking> parkings = Arrays.asList(testParking);
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
    void getAllParkingsAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.getAllParkings());
        verify(parkingService, never()).getAllParkings();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateParkingAsAdminSuccess() {
        // Arrange
        when(parkingService.updateParking(anyLong(), any(ParkingRequest.class))).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.updateParking(1L, parkingRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Parking", response.getBody().getName());
        verify(parkingService).updateParking(1L, parkingRequest);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void updateParkingAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.updateParking(1L, parkingRequest));
        verify(parkingService, never()).updateParking(anyLong(), any(ParkingRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteParkingAsAdminSuccess() {
        // Act
        ResponseEntity<Void> response = parkingController.deleteParking(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(parkingService).deleteParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void deleteParkingAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.deleteParking(1L));
        verify(parkingService, never()).deleteParking(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void associatePartnerToParkingAsAdminSuccess() {
        // Arrange
        when(parkingService.associatePartnerToParking(anyLong(), anyLong())).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.associatePartnerToParking(1L, associateRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Parking", response.getBody().getName());
        verify(parkingService).associatePartnerToParking(1L, associateRequest.getPartnerId());
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void associatePartnerToParkingAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.associatePartnerToParking(1L, associateRequest));
        verify(parkingService, never()).associatePartnerToParking(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removePartnerFromParkingAsAdminSuccess() {
        // Arrange
        when(parkingService.removePartnerFromParking(anyLong())).thenReturn(testParking);

        // Act
        ResponseEntity<Parking> response = parkingController.removePartnerFromParking(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Parking", response.getBody().getName());
        verify(parkingService).removePartnerFromParking(1L);
    }

    @Test
    @WithMockUser(roles = "SOCIO")
    void removePartnerFromParkingAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.removePartnerFromParking(1L));
        verify(parkingService, never()).removePartnerFromParking(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getParkingsWithoutPartnerAsAdminSuccess() {
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
    void getParkingsWithoutPartnerAsSocioThrowsAccessDeniedException() {
        // Act & Assert
        assertThrows(org.springframework.security.access.AccessDeniedException.class, () ->
            parkingController.getParkingsWithoutPartner());
        verify(parkingService, never()).getParkingsWithoutPartner();
    }
}
