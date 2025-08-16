package com.nelumbo.parking.services;

import com.nelumbo.parking.dto.LoginRequest;
import com.nelumbo.parking.dto.RegisterRequest;
import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.exceptions.AuthenticationException;
import com.nelumbo.parking.exceptions.ValidationException;
import com.nelumbo.parking.repositories.UserRepository;
import com.nelumbo.parking.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
class AuthServiceTest {

    // ⬇️ Asegúrate de tener este campo declarado
    @MockitoBean private UserRepository userRepository;
    @MockitoBean private BCryptPasswordEncoder passwordEncoder;
    @MockitoBean private AuthenticationManager authenticationManager;
    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(Role.SOCIO)
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setName("New User");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.SOCIO);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
    }

    @Test
    void register_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("User registered successfully by administrator", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsValidationException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(ValidationException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        String result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("jwtToken", result);
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtUtil).generateToken(authentication);
    }

    @Test
    void login_InvalidCredentials_ThrowsAuthenticationException() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
    }

    @Test
    void logout_WithBearerToken_Success() {
        String authorizationHeader = "Bearer validToken";

        String result = authService.logout(authorizationHeader);

        assertEquals("Logout successful. Token has been invalidated.", result);
        verify(jwtUtil).invalidateToken("validToken");
    }

    @Test
    void logout_WithoutBearerToken_Success() {
        String authorizationHeader = "InvalidHeader";

        String result = authService.logout(authorizationHeader);

        assertEquals("Logout successful. Please remove the token from client storage.", result);
        verify(jwtUtil, never()).invalidateToken(anyString());
    }
}
