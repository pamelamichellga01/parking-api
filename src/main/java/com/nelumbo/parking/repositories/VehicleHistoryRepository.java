package com.nelumbo.parking.repositories;

import com.nelumbo.parking.entities.VehicleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleHistoryRepository extends JpaRepository<VehicleHistory, Long> {

    
    List<VehicleHistory> findByParkingId(Long parkingId);

    
    List<VehicleHistory> findByParkingIdAndExitDateTimeBetween(Long parkingId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    
    long countByExitDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    
    List<VehicleHistory> findByExitDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
