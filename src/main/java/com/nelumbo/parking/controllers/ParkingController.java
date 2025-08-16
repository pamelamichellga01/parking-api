package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.ParkingRequest;
import com.nelumbo.parking.dto.AssociatePartnerRequest;
import com.nelumbo.parking.entities.Parking;
import com.nelumbo.parking.services.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/parkings")
@RequiredArgsConstructor
public class ParkingController {
    
    private final ParkingService parkingService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parking> createParking(@Valid @RequestBody ParkingRequest request) {
        Parking parking = parkingService.createParking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(parking);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SOCIO')")
    public ResponseEntity<Parking> getParkingById(@PathVariable Long id) {
        Parking parking = parkingService.getParkingById(id);
        return ResponseEntity.ok(parking);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Parking>> getAllParkings() {
        List<Parking> parkings = parkingService.getAllParkings();
        return ResponseEntity.ok(parkings);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parking> updateParking(@PathVariable Long id, @Valid @RequestBody ParkingRequest request) {
        Parking parking = parkingService.updateParking(id, request);
        return ResponseEntity.ok(parking);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParking(@PathVariable Long id) {
        parkingService.deleteParking(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/partner/{partnerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SOCIO') and #partnerId == authentication.principal.id)")
    public ResponseEntity<List<Parking>> getParkingsByPartner(@PathVariable Long partnerId) {
        List<Parking> parkings = parkingService.getParkingsByPartner(partnerId);
        return ResponseEntity.ok(parkings);
    }
    
    @GetMapping("/partner/email/{partnerEmail}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('SOCIO') and #partnerEmail == authentication.principal.username)")
    public ResponseEntity<List<Parking>> getParkingsByPartnerEmail(@PathVariable String partnerEmail) {
        List<Parking> parkings = parkingService.getParkingsByPartnerEmail(partnerEmail);
        return ResponseEntity.ok(parkings);
    }
    
    @PostMapping("/{parkingId}/associate-partner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parking> associatePartnerToParking(
            @PathVariable Long parkingId,
            @Valid @RequestBody AssociatePartnerRequest request) {
        Parking parking = parkingService.associatePartnerToParking(parkingId, request.getPartnerId());
        return ResponseEntity.ok(parking);
    }
    
    @DeleteMapping("/{parkingId}/partner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Parking> removePartnerFromParking(@PathVariable Long parkingId) {
        Parking parking = parkingService.removePartnerFromParking(parkingId);
        return ResponseEntity.ok(parking);
    }
    
    @GetMapping("/{parkingId}/has-partner")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SOCIO')")
    public ResponseEntity<Boolean> hasPartner(@PathVariable Long parkingId) {
        boolean hasPartner = parkingService.hasPartner(parkingId);
        return ResponseEntity.ok(hasPartner);
    }
    
    @GetMapping("/without-partner")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Parking>> getParkingsWithoutPartner() {
        List<Parking> parkings = parkingService.getParkingsWithoutPartner();
        return ResponseEntity.ok(parkings);
    }
}
