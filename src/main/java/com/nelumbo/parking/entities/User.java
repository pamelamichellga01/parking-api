package com.nelumbo.parking.entities;

import com.nelumbo.parking.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

     @Column(nullable = false, unique = true)
    private String email;

     @Column(nullable = false)
    private String password;

     @Enumerated(EnumType.STRING)
    private Role role;
}
