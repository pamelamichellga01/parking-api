package com.nelumbo.parking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "parkings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Double hourlyRate;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private User partner;

    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles;
}
