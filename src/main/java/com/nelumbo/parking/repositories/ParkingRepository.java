package com.nelumbo.parking.repositories;

import com.nelumbo.parking.entities.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingRepository extends JpaRepository<Parking, Long> {
    
    Optional<Parking> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT p FROM Parking p WHERE p.partner.id = :partnerId")
    List<Parking> findByPartnerId(@Param("partnerId") Long partnerId);
    
    @Query("SELECT p FROM Parking p WHERE p.partner.email = :partnerEmail")
    List<Parking> findByPartnerEmail(@Param("partnerEmail") String partnerEmail);
}
