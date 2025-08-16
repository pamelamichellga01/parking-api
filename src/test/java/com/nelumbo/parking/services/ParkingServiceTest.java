package com.nelumbo.parking.services;

import com.nelumbo.parking.dto.ParkingRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.ParkingRepository;
import com.nelumbo.parking.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParkingService parkingService;

    private Parking testParking;
    private User testPartner;
    private ParkingRequest parkingRequest;

    @BeforeEach
    void setUp() {
        testPartner = User.builder()
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
    }

    @Test
    void createParking_Success() {
        // Arrange
        when(parkingRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(parkingRepository.save(any(Parking.class))).thenReturn(testParking);

        // Act
        Parking result = parkingService.createParking(parkingRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Parking", result.getName());
        verify(parkingRepository).save(any(Parking.class));
    }

    @Test
    void createParking_WithoutPartner_Success() {
        // Arrange
        parkingRequest.setPartnerId(null);
        when(parkingRepository.existsByName(anyString())).thenReturn(false);
        when(parkingRepository.save(any(Parking.class))).thenReturn(testParking);

        // Act
        Parking result = parkingService.createParking(parkingRequest);

        // Assert
        assertNotNull(result);
        verify(parkingRepository).save(any(Parking.class));
    }

    @Test
    void createParking_NameAlreadyExists_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.existsByName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> parkingService.createParking(parkingRequest));

        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void createParking_PartnerNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> parkingService.createParking(parkingRequest));

        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void createParking_PartnerNotSocio_ThrowsValidationException() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .name("Admin User")
                .email("admin@test.com")
                .password("password")
                .role(Role.ADMIN)
                .build();

        when(parkingRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> parkingService.createParking(parkingRequest));

        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void getParkingById_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));

        // Act
        Parking result = parkingService.getParkingById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Parking", result.getName());
    }

    @Test
    void getParkingById_NotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> parkingService.getParkingById(999L));
    }

    @Test
    void getAllParkings_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingRepository.findAll()).thenReturn(parkings);

        // Act
        List<Parking> result = parkingService.getAllParkings();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Parking", result.getFirst().getName());
    }

    @Test
    void updateParking_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(parkingRepository.save(any(Parking.class))).thenReturn(testParking);

        // Act
        Parking result = parkingService.updateParking(1L, parkingRequest);

        // Assert
        assertNotNull(result);
        verify(parkingRepository).save(any(Parking.class));
    }

    @Test
    void updateParking_NameAlreadyExists_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRepository.existsByName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            parkingService.updateParking(1L, parkingRequest));
        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void updateParking_PartnerNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRepository.existsByName(anyString())).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            parkingService.updateParking(1L, parkingRequest));
        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void deleteParking_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        doNothing().when(parkingRepository).delete(any(Parking.class));

        // Act
        assertDoesNotThrow(() -> parkingService.deleteParking(1L));

        // Assert
        verify(parkingRepository).delete(testParking);
    }

    @Test
    void getParkingsByPartner_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingRepository.findByPartnerId(1L)).thenReturn(parkings);

        // Act
        List<Parking> result = parkingService.getParkingsByPartner(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Parking", result.getFirst().getName());
    }

    @Test
    void getParkingsByPartnerEmail_Success() {
        // Arrange
        List<Parking> parkings = List.of(testParking);
        when(parkingRepository.findByPartnerEmail("partner@test.com")).thenReturn(parkings);

        // Act
        List<Parking> result = parkingService.getParkingsByPartnerEmail("partner@test.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Parking", result.getFirst().getName());
    }

    @Test
    void associatePartnerToParking_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testPartner));
        when(parkingRepository.save(any(Parking.class))).thenReturn(testParking);

        // Act
        Parking result = parkingService.associatePartnerToParking(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(testPartner, result.getPartner());
        verify(parkingRepository).save(any(Parking.class));
    }

    @Test
    void associatePartnerToParking_PartnerNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            parkingService.associatePartnerToParking(1L, 1L));
        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void associatePartnerToParking_PartnerNotSocio_ThrowsValidationException() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .name("Admin User")
                .email("admin@test.com")
                .password("password")
                .role(Role.ADMIN)
                .build();

        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));

        // Act & Assert
        assertThrows(ValidationException.class, () ->
            parkingService.associatePartnerToParking(1L, 1L));
        verify(parkingRepository, never()).save(any(Parking.class));
    }

    @Test
    void removePartnerFromParking_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRepository.save(any(Parking.class))).thenReturn(testParking);

        // Act
        Parking result = parkingService.removePartnerFromParking(1L);

        // Assert
        assertNotNull(result);
        assertNull(result.getPartner());
        verify(parkingRepository).save(any(Parking.class));
    }

    @Test
    void hasPartner_WithPartner_ReturnsTrue() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));

        // Act
        boolean result = parkingService.hasPartner(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasPartner_WithoutPartner_ReturnsFalse() {
        // Arrange
        Parking parkingWithoutPartner = Parking.builder()
                .id(2L)
                .name("Parking Without Partner")
                .capacity(30)
                .hourlyRate(BigDecimal.valueOf(3.00))
                .partner(null)
                .build();

        when(parkingRepository.findById(2L)).thenReturn(Optional.of(parkingWithoutPartner));

        // Act
        boolean result = parkingService.hasPartner(2L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getParkingsWithoutPartner_Success() {
        // Arrange
        Parking parkingWithoutPartner = Parking.builder()
                .id(2L)
                .name("Parking Without Partner")
                .capacity(30)
                .hourlyRate(BigDecimal.valueOf(3.00))
                .partner(null)
                .build();

        List<Parking> allParkings = Arrays.asList(testParking, parkingWithoutPartner);
        when(parkingRepository.findAll()).thenReturn(allParkings);

        // Act
        List<Parking> result = parkingService.getParkingsWithoutPartner();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Parking Without Partner", result.getFirst().getName());
    }
}
