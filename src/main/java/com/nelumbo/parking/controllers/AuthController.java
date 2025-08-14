package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.LoginRequest;
import com.nelumbo.parking.dto.RegisterRequest;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.repositories.UserRepository;
import com.nelumbo.parking.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = jwtUtil.generateToken(authentication);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Verificar que el usuario autenticado sea ADMIN
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        
        // Verificar que el usuario tenga rol ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            return ResponseEntity.status(403).body("Only administrators can register new users");
        }

        // Validaciones del usuario a registrar
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        
        if (request.getRole() != Role.SOCIO) {
            return ResponseEntity.badRequest().body("Only SOCIO role can be registered by administrators");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully by administrator");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            jwtUtil.invalidateToken(token);
            return ResponseEntity.ok("Logout successful. Token has been invalidated.");
        }
        return ResponseEntity.ok("Logout successful. Please remove the token from client storage.");
    }
}
