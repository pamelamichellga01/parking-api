package com.nelumbo.parking.controllers;

import com.nelumbo.parking.services.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-vehicles-all-parkings")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIO')")
    public ResponseEntity<List<Map<String, Object>>> getTopVehiclesAllParkings(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> topVehicles = reportService.getTopVehiclesAllParkings(limit);
        return ResponseEntity.ok(topVehicles);
    }

    @GetMapping("/parking/{parkingId}/top-vehicles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIO')")
    public ResponseEntity<List<Map<String, Object>>> getTopVehiclesByParking(
            @PathVariable Long parkingId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> topVehicles = reportService.getTopVehiclesByParking(parkingId, limit);
        return ResponseEntity.ok(topVehicles);
    }

    
    @GetMapping("/parking/{parkingId}/first-time-vehicles")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOCIO')")
    public ResponseEntity<List<Map<String, Object>>> getFirstTimeVehiclesByParking(
            @PathVariable Long parkingId) {
        
        List<Map<String, Object>> firstTimeVehicles = reportService.getFirstTimeVehiclesByParking(parkingId);
        return ResponseEntity.ok(firstTimeVehicles);
    }

    
    @GetMapping("/parking/{parkingId}/earnings-period")
    @PreAuthorize("hasRole('SOCIO')")
    public ResponseEntity<Map<String, Object>> getEarningsByPeriod(
            @PathVariable Long parkingId,
            @RequestParam String period) {
        
        Map<String, Object> earnings = reportService.getEarningsByPeriod(parkingId, period);
        return ResponseEntity.ok(earnings);
    }

    
    @GetMapping("/parking/{parkingId}/earnings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getEarningsByParkingAndDate(
            @PathVariable Long parkingId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        Map<String, Object> earnings = reportService.getEarningsByParkingAndDate(parkingId, date);
        return ResponseEntity.ok(earnings);
    }

    
    @GetMapping("/all-parkings/earnings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllParkingsEarningsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<Map<String, Object>> allEarnings = reportService.getAllParkingsEarningsByDate(date);
        return ResponseEntity.ok(allEarnings);
    }

    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        
        Map<String, Object> statistics = reportService.getGeneralStatistics();
        return ResponseEntity.ok(statistics);
    }
}
