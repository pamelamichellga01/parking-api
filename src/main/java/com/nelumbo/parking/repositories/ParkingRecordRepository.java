package com.nelumbo.parking.repositories;

import com.nelumbo.parking.entities.ParkingRecord;
import com.nelumbo.parking.entities.ParkingRecord.ParkingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingRecordRepository extends JpaRepository<ParkingRecord, Long> {
    
    @Query("SELECT pr FROM ParkingRecord pr WHERE pr.vehicle.licensePlate = :licensePlate AND pr.status = 'PARKED'")
    Optional<ParkingRecord> findActiveByLicensePlate(@Param("licensePlate") String licensePlate);
    
    @Query("SELECT pr FROM ParkingRecord pr WHERE pr.parking.id = :parkingId AND pr.status = 'PARKED'")
    List<ParkingRecord> findActiveByParkingId(@Param("parkingId") Long parkingId);
    
    @Query("SELECT COUNT(pr) FROM ParkingRecord pr WHERE pr.parking.id = :parkingId AND pr.status = 'PARKED'")
    Long countActiveByParkingId(@Param("parkingId") Long parkingId);
    
    @Query("SELECT pr FROM ParkingRecord pr WHERE pr.vehicle.licensePlate = :licensePlate AND pr.parking.id = :parkingId AND pr.status = 'PARKED'")
    Optional<ParkingRecord> findActiveByLicensePlateAndParking(@Param("licensePlate") String licensePlate, @Param("parkingId") Long parkingId);
    
    @Query("SELECT pr FROM ParkingRecord pr WHERE pr.parking.id = :parkingId AND pr.status = 'EXITED' AND pr.exitDateTime BETWEEN :startDate AND :endDate")
    List<ParkingRecord> findExitedByParkingAndDateRange(@Param("parkingId") Long parkingId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT pr.vehicle.licensePlate, COUNT(pr) FROM ParkingRecord pr GROUP BY pr.vehicle.licensePlate ORDER BY COUNT(pr) DESC")
    List<Object[]> findTopVehiclesByRegistrationCount();
    
    @Query("SELECT pr.vehicle.licensePlate, COUNT(pr) FROM ParkingRecord pr WHERE pr.parking.id = :parkingId GROUP BY pr.vehicle.licensePlate ORDER BY COUNT(pr) DESC")
    List<Object[]> findTopVehiclesByParkingId(@Param("parkingId") Long parkingId);
}
