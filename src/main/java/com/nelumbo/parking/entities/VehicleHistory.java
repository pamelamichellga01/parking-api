package com.nelumbo.parking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String licensePlate;

    @Column(nullable = false)
    private LocalDateTime entryDateTime;

    @Column(nullable = false)
    private LocalDateTime exitDateTime;

    @ManyToOne
    @JoinColumn(name = "parking_id")
    private Parking parking;
}
