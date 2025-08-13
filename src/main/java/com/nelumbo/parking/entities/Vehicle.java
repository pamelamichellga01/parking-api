package com.nelumbo.parking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicle_license_plate", columnList = "license_plate", unique = true),
                @Index(name = "idx_vehicle_parking_lot", columnList = "parking_lot_id")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Adjust size if your plate format allows 7 chars
    @NotBlank
    @Size(min = 5, max = 7)
    @Column(name = "license_plate", nullable = false, length = 7, unique = true)
    private String licensePlate;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @PrePersist @PreUpdate
    private void normalize() {
        if (licensePlate != null) licensePlate = licensePlate.trim().toUpperCase();
    }
}
