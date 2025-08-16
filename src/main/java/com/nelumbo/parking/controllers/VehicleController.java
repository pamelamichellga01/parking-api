package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.VehicleEntryRequest;
import com.nelumbo.parking.dto.VehicleExitRequest;
import com.nelumbo.parking.entities.ParkingRecord;
import com.nelumbo.parking.entities.Vehicle;
import com.nelumbo.parking.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    
    private final VehicleService vehicleService;
    
    @PostMapping("/entry")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<VehicleEntryResponse> registerVehicleEntry(@Valid @RequestBody VehicleEntryRequest request) {
        Long recordId = vehicleService.registerVehicleEntry(request);
        VehicleEntryResponse response = new VehicleEntryResponse(recordId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/exit")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<VehicleExitResponse> registerVehicleExit(@Valid @RequestBody VehicleExitRequest request) {
        String message = vehicleService.registerVehicleExit(request);
        VehicleExitResponse response = new VehicleExitResponse(message);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/parked/{parkingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SOCIO')")
    public ResponseEntity<List<ParkingRecord>> getParkedVehicles(@PathVariable Long parkingId) {
        List<ParkingRecord> parkedVehicles = vehicleService.getParkedVehicles(parkingId);
        return ResponseEntity.ok(parkedVehicles);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SOCIO')")
    public ResponseEntity<List<Vehicle>> searchVehiclesByPlate(@RequestParam String plate) {
        List<Vehicle> vehicles = vehicleService.searchVehiclesByPlate(plate);
        return ResponseEntity.ok(vehicles);
    }
    
    
    public static class VehicleEntryResponse {
        private Long id;
        
        public VehicleEntryResponse(Long id) {
            this.id = id;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
    }
    
    public static class VehicleExitResponse {
        private String mensaje;
        
        public VehicleExitResponse(String mensaje) {
            this.mensaje = mensaje;
        }
        
        public String getMensaje() {
            return mensaje;
        }
        
        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}
