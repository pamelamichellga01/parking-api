package com.nelumbo.parking.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "invalid_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvalidToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String token;
    
    @Column(nullable = false)
    private LocalDateTime invalidatedAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
