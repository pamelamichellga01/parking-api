package com.nelumbo.parking.services;

import com.nelumbo.parking.dto.LoginRequest;
import com.nelumbo.parking.dto.RegisterRequest;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.exceptions.AuthenticationException;
import com.nelumbo.parking.exceptions.AuthorizationException;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.UserRepository;
import com.nelumbo.parking.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public String login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            return jwtUtil.generateToken(authentication);
        } catch (Exception e) {
            throw new AuthenticationException("Invalid credentials", e);
        }
    }

    public String register(RegisterRequest request) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Authentication required");
        }
        
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new AuthorizationException("Only administrators can register new users");
        }

        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Email already in use");
        }
        
        if (request.getRole() != Role.SOCIO) {
            throw new ValidationException("Only SOCIO role can be registered by administrators");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
        return "User registered successfully by administrator";
    }

    public String logout(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            jwtUtil.invalidateToken(token);
            return "Logout successful. Token has been invalidated.";
        }
        return "Logout successful. Please remove the token from client storage.";
    }
}
