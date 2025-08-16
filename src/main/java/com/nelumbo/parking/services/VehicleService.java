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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ParkingRepository parkingRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final EmailService emailService;

    @Transactional
    public Long registerVehicleEntry(VehicleEntryRequest request) {
        
        String licensePlate = request.getLicensePlate().toUpperCase();

        
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new ValidationException("Parqueadero no encontrado"));

        
        if (parkingRecordRepository.findActiveByLicensePlate(licensePlate).isPresent()) {
            throw new ValidationException("No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero");
        }
 // Verificar capacidad del parqueadero
        Long currentOccupancy = parkingRecordRepository.countActiveByParkingId(parking.getId());
        if (currentOccupancy >= parking.getCapacity()) {
            throw new ValidationException("El parqueadero está lleno. No se puede registrar más vehículos");
        }

        
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> {
                    Vehicle newVehicle = Vehicle.builder()
                            .licensePlate(licensePlate)
                            .build();
                    return vehicleRepository.save(newVehicle);
                });

       
        ParkingRecord parkingRecord = ParkingRecord.builder()
                .vehicle(vehicle)
                .parking(parking)
                .entryDateTime(LocalDateTime.now())
                .status(ParkingStatus.PARKED)
                .build();

        ParkingRecord savedRecord = parkingRecordRepository.save(parkingRecord);
        
        
        sendEntryEmail(licensePlate, parking.getName(), "Vehículo registrado exitosamente");
        
        return savedRecord.getId();
    }

    @Transactional
    public String registerVehicleExit(VehicleExitRequest request) {
        String licensePlate = request.getLicensePlate().toUpperCase();

        
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new ValidationException("Parqueadero no encontrado"));

        
        ParkingRecord parkingRecord = parkingRecordRepository.findActiveByLicensePlateAndParking(licensePlate, parking.getId())
                .orElseThrow(() -> new ValidationException("No se puede Registrar Salida, no existe la placa en el parqueadero"));

        
        LocalDateTime exitDateTime = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(parkingRecord.getEntryDateTime(), exitDateTime);
        long minutes = ChronoUnit.MINUTES.between(parkingRecord.getEntryDateTime(), exitDateTime) % 60;

        
        BigDecimal totalHours = BigDecimal.valueOf(hours);
        if (minutes > 0) {
            totalHours = totalHours.add(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.UP));
        }
        if (totalHours.compareTo(BigDecimal.ONE) < 0) {
            totalHours = BigDecimal.ONE;
        }

        BigDecimal totalCost = parking.getHourlyRate().multiply(totalHours);

        
        parkingRecord.setExitDateTime(exitDateTime);
        parkingRecord.setTotalCost(totalCost);
        parkingRecord.setStatus(ParkingStatus.EXITED);
        parkingRecordRepository.save(parkingRecord);

        
        VehicleHistory history = VehicleHistory.builder()
                .licensePlate(licensePlate)
                .parkingName(parking.getName())
                .entryDateTime(parkingRecord.getEntryDateTime())
                .exitDateTime(exitDateTime)
                .totalCost(totalCost)
                .parkingId(parking.getId())
                .vehicleId(parkingRecord.getVehicle().getId())
                .build();

        vehicleHistoryRepository.save(history);

        
        String mensajeSalida = String.format("Vehículo salió del parqueadero. Costo total: $%.2f", totalCost);
        sendExitEmail(licensePlate, parking.getName(), mensajeSalida);

        return "Salida registrada";
    }

    public List<ParkingRecord> getParkedVehicles(Long parkingId) {
        
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        return parkingRecordRepository.findActiveByParkingId(parkingId);
    }

    public List<Vehicle> searchVehiclesByPlate(String partialPlate) {
        if (partialPlate == null || partialPlate.trim().isEmpty()) {
            throw new ValidationException("La placa parcial no puede estar vacía");
        }

        return vehicleRepository.findByLicensePlateContaining(partialPlate.toUpperCase());
    }

    
    private void sendEntryEmail(String placa, String parqueaderoNombre, String mensaje) {
        try {
            
            String email = "usuario@ejemplo.com";
            emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de entrada: {}", e.getMessage());
        }
    }

    
    private void sendExitEmail(String placa, String parqueaderoNombre, String mensaje) {
        try {
            
            String email = "usuario@ejemplo.com";
            emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de salida: {}", e.getMessage());
        }
    }
}
