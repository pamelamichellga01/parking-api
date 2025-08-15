package com.nelumbo.parking.services;

import com.nelumbo.parking.entities.VehicleHistory;
import com.nelumbo.parking.entities.ParkingRecord;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.VehicleHistoryRepository;
import com.nelumbo.parking.repositories.ParkingRecordRepository;
import com.nelumbo.parking.repositories.ParkingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final ParkingRepository parkingRepository;

    /**
     * Obtiene el top de vehículos más registrados en un parqueadero
     * 
     * @param parkingId ID del parqueadero
     * @param limit Límite de resultados (por defecto 10)
     * @return Lista de vehículos con su conteo de registros
     */
    public List<Map<String, Object>> getTopVehiclesByParking(Long parkingId, int limit) {
        // Verificar que el parqueadero existe
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        // Obtener historial del parqueadero
        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingId(parkingId);
        
        // Agrupar por placa y contar
        Map<String, Long> vehicleCounts = history.stream()
                .collect(Collectors.groupingBy(
                        VehicleHistory::getLicensePlate,
                        Collectors.counting()
                ));

        // Ordenar por conteo descendente y limitar resultados
        return vehicleCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("placa", entry.getKey());
                    result.put("totalRegistros", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene vehículos de primera vez en un parqueadero (solo una visita)
     * 
     * @param parkingId ID del parqueadero
     * @return Lista de vehículos de primera vez
     */
    public List<Map<String, Object>> getFirstTimeVehiclesByParking(Long parkingId) {
        // Verificar que el parqueadero existe
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        // Obtener historial del parqueadero
        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingId(parkingId);
        
        // Agrupar por placa y contar
        Map<String, Long> vehicleCounts = history.stream()
                .collect(Collectors.groupingBy(
                        VehicleHistory::getLicensePlate,
                        Collectors.counting()
                ));

        // Filtrar solo los que tienen 1 registro (primera vez)
        return vehicleCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("placa", entry.getKey());
                    result.put("totalVisitas", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las ganancias de un parqueadero por fecha
     * 
     * @param parkingId ID del parqueadero
     * @param date Fecha específica (opcional, por defecto hoy)
     * @return Ganancias totales y detalles
     */
    public Map<String, Object> getEarningsByParkingAndDate(Long parkingId, LocalDate date) {
        // Verificar que el parqueadero existe
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        // Si no se especifica fecha, usar hoy
        if (date == null) {
            date = LocalDate.now();
        }

        // Obtener historial del parqueadero para la fecha específica
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingIdAndExitDateTimeBetween(
                parkingId, startOfDay, endOfDay);

        // Calcular ganancias totales
        BigDecimal totalEarnings = history.stream()
                .map(VehicleHistory::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Contar vehículos
        int totalVehicles = history.size();

        // Preparar respuesta
        Map<String, Object> result = new HashMap<>();
        result.put("parkingId", parkingId);
        result.put("fecha", date);
        result.put("totalVehiculos", totalVehicles);
        result.put("gananciasTotales", totalEarnings);
        result.put("detalle", history.stream()
                .map(h -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put("placa", h.getLicensePlate());
                    detail.put("entrada", h.getEntryDateTime());
                    detail.put("salida", h.getExitDateTime());
                    detail.put("costo", h.getTotalCost());
                    return detail;
                })
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * Obtiene las ganancias de todos los parqueaderos por fecha
     * 
     * @param date Fecha específica (opcional, por defecto hoy)
     * @return Ganancias por parqueadero
     */
    public List<Map<String, Object>> getAllParkingsEarningsByDate(LocalDate date) {
        // Si no se especifica fecha, usar hoy
        final LocalDate finalDate = (date != null) ? date : LocalDate.now();

        // Obtener todos los parqueaderos
        List<com.nelumbo.parking.entities.Parking> parkings = parkingRepository.findAll();
        
        return parkings.stream()
                .map(parking -> getEarningsByParkingAndDate(parking.getId(), finalDate))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el top de vehículos más registrados en TODOS los parqueaderos
     * 
     * @param limit Límite de resultados (por defecto 10)
     * @return Lista de vehículos con su conteo de registros
     */
    public List<Map<String, Object>> getTopVehiclesAllParkings(int limit) {
        // Obtener historial de todos los parqueaderos
        List<VehicleHistory> allHistory = vehicleHistoryRepository.findAll();
        
        // Agrupar por placa y contar
        Map<String, Long> vehicleCounts = allHistory.stream()
                .collect(Collectors.groupingBy(
                        VehicleHistory::getLicensePlate,
                        Collectors.counting()
                ));

        // Ordenar por conteo descendente y limitar resultados
        return vehicleCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("placa", entry.getKey());
                    result.put("totalRegistros", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las ganancias de un parqueadero por diferentes períodos
     * 
     * @param parkingId ID del parqueadero
     * @param period Período: "today", "week", "month", "year"
     * @return Ganancias del período
     */
    public Map<String, Object> getEarningsByPeriod(Long parkingId, String period) {
        // Verificar que el parqueadero existe
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (period.toLowerCase()) {
            case "today":
                startDate = LocalDate.now();
                break;
            case "week":
                startDate = LocalDate.now().minusWeeks(1);
                break;
            case "month":
                startDate = LocalDate.now().minusMonths(1);
                break;
            case "year":
                startDate = LocalDate.now().minusYears(1);
                break;
            default:
                throw new ValidationException("Período no válido. Use: today, week, month, year");
        }

        // Obtener historial del parqueadero para el período
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingIdAndExitDateTimeBetween(
                parkingId, startDateTime, endDateTime);

        // Calcular ganancias totales
        BigDecimal totalEarnings = history.stream()
                .map(VehicleHistory::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Contar vehículos
        int totalVehicles = history.size();

        // Preparar respuesta
        Map<String, Object> result = new HashMap<>();
        result.put("parkingId", parkingId);
        result.put("periodo", period);
        result.put("fechaInicio", startDate);
        result.put("fechaFin", endDate);
        result.put("totalVehiculos", totalVehicles);
        result.put("gananciasTotales", totalEarnings);
        
        return result;
    }

    /**
     * Obtiene estadísticas generales del sistema
     * 
     * @return Estadísticas generales
     */
    public Map<String, Object> getGeneralStatistics() {
        // Total de parqueaderos
        long totalParkings = parkingRepository.count();
        
        // Total de vehículos registrados hoy
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);
        
        long totalVehiclesToday = vehicleHistoryRepository.countByExitDateTimeBetween(startOfDay, endOfDay);
        
        // Ganancias totales de hoy
        BigDecimal totalEarningsToday = vehicleHistoryRepository.findByExitDateTimeBetween(startOfDay, endOfDay)
                .stream()
                .map(VehicleHistory::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Vehículos actualmente estacionados
        long currentlyParked = parkingRecordRepository.countActiveByParkingId(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("fecha", today);
        stats.put("totalParqueaderos", totalParkings);
        stats.put("vehiculosRegistradosHoy", totalVehiclesToday);
        stats.put("gananciasHoy", totalEarningsToday);
        stats.put("vehiculosEstacionados", currentlyParked);
        
        return stats;
    }
}
