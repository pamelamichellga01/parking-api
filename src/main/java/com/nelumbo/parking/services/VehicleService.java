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
        // Validar formato de placa (ya validado en DTO)
        String licensePlate = request.getLicensePlate().toUpperCase();

        // Verificar que el parqueadero existe
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new ValidationException("Parqueadero no encontrado"));

        // Verificar que la placa no esté ya en ningún parqueadero
        if (parkingRecordRepository.findActiveByLicensePlate(licensePlate).isPresent()) {
            throw new ValidationException("No se puede Registrar Ingreso, ya existe la placa en este u otro parqueadero");
        }

        // Verificar capacidad del parqueadero
        Long currentOccupancy = parkingRecordRepository.countActiveByParkingId(parking.getId());
        if (currentOccupancy >= parking.getCapacity()) {
            throw new ValidationException("El parqueadero está lleno. No se puede registrar más vehículos");
        }

        // Crear o obtener el vehículo
        Vehicle vehicle = vehicleRepository.findByLicensePlate(licensePlate)
                .orElseGet(() -> {
                    Vehicle newVehicle = Vehicle.builder()
                            .licensePlate(licensePlate)
                            .build();
                    return vehicleRepository.save(newVehicle);
                });

        // Crear el registro de entrada
        ParkingRecord parkingRecord = ParkingRecord.builder()
                .vehicle(vehicle)
                .parking(parking)
                .entryDateTime(LocalDateTime.now())
                .status(ParkingStatus.PARKED)
                .build();

        ParkingRecord savedRecord = parkingRecordRepository.save(parkingRecord);
        
        // NUEVO: Llamar al microservicio de email para notificar entrada
        sendEntryEmail(licensePlate, parking.getName(), "Vehículo registrado exitosamente");
        
        return savedRecord.getId();
    }

    @Transactional
    public String registerVehicleExit(VehicleExitRequest request) {
        String licensePlate = request.getLicensePlate().toUpperCase();

        // Verificar que el parqueadero existe
        Parking parking = parkingRepository.findById(request.getParkingId())
                .orElseThrow(() -> new ValidationException("Parqueadero no encontrado"));

        // Verificar que el vehículo esté en ese parqueadero
        ParkingRecord parkingRecord = parkingRecordRepository.findActiveByLicensePlateAndParking(licensePlate, parking.getId())
                .orElseThrow(() -> new ValidationException("No se puede Registrar Salida, no existe la placa en el parqueadero"));

        // Calcular tiempo de estadía y costo
        LocalDateTime exitDateTime = LocalDateTime.now();
        long hours = ChronoUnit.HOURS.between(parkingRecord.getEntryDateTime(), exitDateTime);
        long minutes = ChronoUnit.MINUTES.between(parkingRecord.getEntryDateTime(), exitDateTime) % 60;

        // Mínimo 1 hora, luego por fracciones de hora
        BigDecimal totalHours = BigDecimal.valueOf(hours);
        if (minutes > 0) {
            totalHours = totalHours.add(BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.UP));
        }
        if (totalHours.compareTo(BigDecimal.ONE) < 0) {
            totalHours = BigDecimal.ONE;
        }

        BigDecimal totalCost = parking.getHourlyRate().multiply(totalHours);

        // Actualizar el registro
        parkingRecord.setExitDateTime(exitDateTime);
        parkingRecord.setTotalCost(totalCost);
        parkingRecord.setStatus(ParkingStatus.EXITED);
        parkingRecordRepository.save(parkingRecord);

        // Crear registro en historial
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

        // NUEVO: Llamar al microservicio de email para notificar salida
        String mensajeSalida = String.format("Vehículo salió del parqueadero. Costo total: $%.2f", totalCost);
        sendExitEmail(licensePlate, parking.getName(), mensajeSalida);

        return "Salida registrada";
    }

    public List<ParkingRecord> getParkedVehicles(Long parkingId) {
        // Verificar que el parqueadero existe
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

    // NUEVO MÉTODO: Enviar email de entrada
    private void sendEntryEmail(String placa, String parqueaderoNombre, String mensaje) {
        try {
            // Por ahora usamos un email genérico ya que no tenemos email del usuario
            String email = "usuario@ejemplo.com";
            emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de entrada: {}", e.getMessage());
        }
    }

    // NUEVO MÉTODO: Enviar email de salida
    private void sendExitEmail(String placa, String parqueaderoNombre, String mensaje) {
        try {
            // Por ahora usamos un email genérico ya que no tenemos email del usuario
            String email = "usuario@ejemplo.com";
            emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);
        } catch (Exception e) {
            log.warn("No se pudo enviar email de salida: {}", e.getMessage());
        }
    }
}
