package com.nelumbo.parking.repositories;

import com.nelumbo.parking.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    
    boolean existsByLicensePlate(String licensePlate);
    
    @Query("SELECT v FROM Vehicle v WHERE v.licensePlate LIKE %:partialPlate%")
    List<Vehicle> findByLicensePlateContaining(@Param("partialPlate") String partialPlate);
}
