package com.nelumbo.parking.services;

import com.nelumbo.parking.dto.VehicleEntryRequest;
import com.nelumbo.parking.dto.VehicleExitRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.entities.ParkingRecord;
import com.nelumbo.parking.entities.Vehicle;
import com.nelumbo.parking.entities.VehicleHistory;
import com.nelumbo.parking.entities.ParkingRecord.ParkingStatus;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.ParkingRecordRepository;
import com.nelumbo.parking.repositories.ParkingRepository;
import com.nelumbo.parking.repositories.VehicleHistoryRepository;
import com.nelumbo.parking.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private ParkingRecordRepository parkingRecordRepository;

    @Mock
    private VehicleHistoryRepository vehicleHistoryRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VehicleService vehicleService;

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
    void registerVehicleEntry_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlate("ABC123")).thenReturn(Optional.empty());
        when(parkingRecordRepository.countActiveByParkingId(1L)).thenReturn(25L);
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(testVehicle));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(testParkingRecord);
        when(emailService.sendEmail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        Long result = vehicleService.registerVehicleEntry(entryRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);
        verify(parkingRecordRepository).save(any(ParkingRecord.class));
        verify(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void registerVehicleEntry_NewVehicle_Success() {
        // Arrange
        Vehicle newVehicle = Vehicle.builder()
                .id(2L)
                .licensePlate("XYZ789")
                .build();

        entryRequest.setLicensePlate("XYZ789");
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlate("XYZ789")).thenReturn(Optional.empty());
        when(parkingRecordRepository.countActiveByParkingId(1L)).thenReturn(25L);
        when(vehicleRepository.findByLicensePlate("XYZ789")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(newVehicle);
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(testParkingRecord);
        when(emailService.sendEmail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        Long result = vehicleService.registerVehicleEntry(entryRequest);

        // Assert
        assertNotNull(result);
        verify(vehicleRepository).save(any(Vehicle.class));
        verify(parkingRecordRepository).save(any(ParkingRecord.class));
    }

    @Test
    void registerVehicleEntry_ParkingNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(999L)).thenReturn(Optional.empty());
        entryRequest.setParkingId(999L);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> vehicleService.registerVehicleEntry(entryRequest));
        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
    }

    @Test
    void registerVehicleEntry_VehicleAlreadyParked_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlate("ABC123")).thenReturn(Optional.of(testParkingRecord));

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> vehicleService.registerVehicleEntry(entryRequest));
        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
    }

    @Test
    void registerVehicleEntry_ParkingFull_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlate("ABC123")).thenReturn(Optional.empty());
        when(parkingRecordRepository.countActiveByParkingId(1L)).thenReturn(50L);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> vehicleService.registerVehicleEntry(entryRequest));
        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
    }

    @Test
    void registerVehicleExit_Success() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlateAndParking("ABC123", 1L))
                .thenReturn(Optional.of(testParkingRecord));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(testParkingRecord);
        when(vehicleHistoryRepository.save(any(VehicleHistory.class))).thenReturn(new VehicleHistory());
        when(emailService.sendEmail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        String result = vehicleService.registerVehicleExit(exitRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Salida registrada", result);
        verify(parkingRecordRepository).save(any(ParkingRecord.class));
        verify(vehicleHistoryRepository).save(any(VehicleHistory.class));
        verify(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void registerVehicleExit_ParkingNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(999L)).thenReturn(Optional.empty());
        exitRequest.setParkingId(999L);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> vehicleService.registerVehicleExit(exitRequest));
        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
    }

    @Test
    void registerVehicleExit_VehicleNotInParking_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlateAndParking("ABC123", 1L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> vehicleService.registerVehicleExit(exitRequest));
        verify(parkingRecordRepository, never()).save(any(ParkingRecord.class));
    }

    @Test
    void getParkedVehicles_Success() {
        // Arrange
        List<ParkingRecord> parkedVehicles = List.of(testParkingRecord);
        when(parkingRepository.existsById(1L)).thenReturn(true);
        when(parkingRecordRepository.findActiveByParkingId(1L)).thenReturn(parkedVehicles);

        // Act
        List<ParkingRecord> result = vehicleService.getParkedVehicles(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC123", result.getFirst().getVehicle().getLicensePlate());
    }

    @Test
    void getParkedVehicles_ParkingNotFound_ThrowsValidationException() {
        // Arrange
        when(parkingRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class,
                () -> vehicleService.getParkedVehicles(999L));
    }

    @Test
    void searchVehiclesByPlate_Success() {
        // Arrange
        List<Vehicle> vehicles = List.of(testVehicle);
        when(vehicleRepository.findByLicensePlateContaining("ABC")).thenReturn(vehicles);

        // Act
        List<Vehicle> result = vehicleService.searchVehiclesByPlate("ABC");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC123", result.getFirst().getLicensePlate());
    }

    @ParameterizedTest(name = "[{index}] placa inválida: {0}")
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   "})
    void searchVehiclesByPlate_InvalidInput_ThrowsValidationException(String plate) {
        assertThrows(ValidationException.class, () -> vehicleService.searchVehiclesByPlate(plate));
        verify(vehicleRepository, never()).findByLicensePlateContaining(anyString());
    }

    @Test
    void registerVehicleEntry_LicensePlateUpperCase_Success() {
        // Arrange
        entryRequest.setLicensePlate("abc123");
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlate("ABC123")).thenReturn(Optional.empty());
        when(parkingRecordRepository.countActiveByParkingId(1L)).thenReturn(25L);
        when(vehicleRepository.findByLicensePlate("ABC123")).thenReturn(Optional.of(testVehicle));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(testParkingRecord);
        when(emailService.sendEmail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        Long result = vehicleService.registerVehicleEntry(entryRequest);

        // Assert
        assertNotNull(result);
        verify(parkingRecordRepository).save(any(ParkingRecord.class));
    }

    @Test
    void registerVehicleExit_LicensePlateUpperCase_Success() {
        // Arrange
        exitRequest.setLicensePlate("abc123");
        when(parkingRepository.findById(1L)).thenReturn(Optional.of(testParking));
        when(parkingRecordRepository.findActiveByLicensePlateAndParking("ABC123", 1L))
                .thenReturn(Optional.of(testParkingRecord));
        when(parkingRecordRepository.save(any(ParkingRecord.class))).thenReturn(testParkingRecord);
        when(vehicleHistoryRepository.save(any(VehicleHistory.class))).thenReturn(new VehicleHistory());
        when(emailService.sendEmail(anyString(), anyString(), anyString(), anyString())).thenReturn(true);

        // Act
        String result = vehicleService.registerVehicleExit(exitRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Salida registrada", result);
    }
}
