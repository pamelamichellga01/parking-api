package com.nelumbo.parking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "parking_history",
        indexes = {
                @Index(name = "idx_hist_license_plate", columnList = "license_plate"),
                @Index(name = "idx_hist_parkinglot_checkin", columnList = "parking_lot_id,check_in_at")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 5, max = 7)
    @Column(name = "license_plate", nullable = false, length = 7)
    private String licensePlate;

    @Column(name = "check_in_at", nullable = false)
    private LocalDateTime checkInAt;

    @Column(name = "check_out_at")
    private LocalDateTime checkOutAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @PrePersist @PreUpdate
    private void normalize() {
        if (licensePlate != null) licensePlate = licensePlate.trim().toUpperCase();
    }
}
