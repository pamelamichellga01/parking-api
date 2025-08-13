package com.nelumbo.parking.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "parking_lots")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "address", nullable = false)
    private String address;

    @Min(1)
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "hourly_rate", precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    // Many parking lots belong to one partner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private User assignedPartner;

    // Vehicles currently parked
    @OneToMany(mappedBy = "parkingLot", orphanRemoval = true)
    private List<Vehicle> vehicles;

    // Parking history
    @OneToMany(mappedBy = "parkingLot", orphanRemoval = true)
    private List<ParkingHistory> history;
}
