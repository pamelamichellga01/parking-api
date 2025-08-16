package com.nelumbo.parking.repositories;

import com.nelumbo.parking.entities.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {

    // NUEVO: Buscar por ID de parqueadero
    List<VehicleHistory> findByParkingId(Long parkingId);

    // NUEVO: Buscar por ID de parqueadero y rango de fechas de salida
    List<VehicleHistory> findByParkingIdAndExitDateTimeBetween(Long parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // NUEVO: Contar por rango de fechas de salida
    long countByExitDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // NUEVO: Buscar por rango de fechas de salida
    List<VehicleHistory> findByExitDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
