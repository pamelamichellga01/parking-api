package com.nelumbo.parking.services;

import com.nelumbo.parking.entities.VehicleHistory;
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

    private static final String KEY_PLACA = "placa"; 

    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final ParkingRecordRepository parkingRecordRepository;
    private final ParkingRepository parkingRepository;

    public List<Map<String, Object>> getTopVehiclesByParking(Long parkingId, int limit) {
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingId(parkingId);

        Map<String, Long> vehicleCounts = history.stream()
                .collect(Collectors.groupingBy(
                        VehicleHistory::getLicensePlate,
                        Collectors.counting()
                ));

        return vehicleCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put(KEY_PLACA, entry.getKey()); 
                    result.put("totalRegistros", entry.getValue());
                    return result;
                })
                .toList(); 
    }

    public List<Map<String, Object>> getFirstTimeVehiclesByParking(Long parkingId) {
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingId(parkingId);

        Map<String, Long> vehicleCounts = history.stream()
                .collect(Collectors.groupingBy(
                        VehicleHistory::getLicensePlate,
                        Collectors.counting()
                ));

        return vehicleCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put(KEY_PLACA, entry.getKey()); 
                    result.put("totalVisitas", entry.getValue());
                    return result;
                })
                .toList(); 
    }

    public Map<String, Object> getEarningsByParkingAndDate(Long parkingId, LocalDate date) {
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        if (date == null) {
            date = LocalDate.now();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingIdAndExitDateTimeBetween(
                parkingId, startOfDay, endOfDay);

        BigDecimal totalEarnings = history.stream()
                .map(VehicleHistory::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalVehicles = history.size();

        Map<String, Object> result = new HashMap<>();
        result.put("parkingId", parkingId);
        result.put("fecha", date);
        result.put("totalVehiculos", totalVehicles);
        result.put("gananciasTotales", totalEarnings);
        result.put("detalle", history.stream()
                .map(h -> {
                    Map<String, Object> detail = new HashMap<>();
                    detail.put(KEY_PLACA, h.getLicensePlate()); 
                    detail.put("entrada", h.getEntryDateTime());
                    detail.put("salida", h.getExitDateTime());
                    detail.put("costo", h.getTotalCost());
                    return detail;
                })
                .toList()); 

        return result;
    }

    public List<Map<String, Object>> getAllParkingsEarningsByDate(LocalDate date) {
        final LocalDate finalDate = (date != null) ? date : LocalDate.now();

        List<com.nelumbo.parking.entities.Parking> parkings = parkingRepository.findAll();

        return parkings.stream()
                .map(parking -> getEarningsByParkingAndDate(parking.getId(), finalDate))
                .toList(); 
    }

    public List<Map<String, Object>> getTopVehiclesAllParkings(int limit) {
        List<VehicleHistory> allHistory = vehicleHistoryRepository.findAll();

        Map<String, Long> vehicleCounts = allHistory.stream()
                .collect(Collectors.groupingBy(
                        VehicleHistory::getLicensePlate,
                        Collectors.counting()
                ));

        return vehicleCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put(KEY_PLACA, entry.getKey()); 
                    result.put("totalRegistros", entry.getValue());
                    return result;
                })
                .toList(); 
    }

    public Map<String, Object> getEarningsByPeriod(Long parkingId, String period) {
        if (!parkingRepository.existsById(parkingId)) {
            throw new ValidationException("Parqueadero no encontrado");
        }

        LocalDate startDate;
        LocalDate endDate = LocalDate.now();

        switch (period.toLowerCase()) {
            case "today" -> startDate = LocalDate.now();
            case "week"  -> startDate = LocalDate.now().minusWeeks(1);
            case "month" -> startDate = LocalDate.now().minusMonths(1);
            case "year"  -> startDate = LocalDate.now().minusYears(1);
            default -> throw new ValidationException("Período no válido. Use: today, week, month, year");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<VehicleHistory> history = vehicleHistoryRepository.findByParkingIdAndExitDateTimeBetween(
                parkingId, startDateTime, endDateTime);

        BigDecimal totalEarnings = history.stream()
                .map(VehicleHistory::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalVehicles = history.size();

        Map<String, Object> result = new HashMap<>();
        result.put("parkingId", parkingId);
        result.put("periodo", period);
        result.put("fechaInicio", startDate);
        result.put("fechaFin", endDate);
        result.put("totalVehiculos", totalVehicles);
        result.put("gananciasTotales", totalEarnings);

        return result;
    }

    public Map<String, Object> getGeneralStatistics() {
        long totalParkings = parkingRepository.count();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long totalVehiclesToday = vehicleHistoryRepository.countByExitDateTimeBetween(startOfDay, endOfDay);

        BigDecimal totalEarningsToday = vehicleHistoryRepository.findByExitDateTimeBetween(startOfDay, endOfDay)
                .stream()
                .map(VehicleHistory::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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