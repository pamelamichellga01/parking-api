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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
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
        // Arrange
        Authentication adminAuth = mock(Authentication.class);
        when(adminAuth.isAuthenticated()).thenReturn(true);
        
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(adminAuth);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            String result = authService.register(registerRequest);

            // Assert
            assertNotNull(result);
            assertEquals("User registered successfully by administrator", result);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void register_EmailAlreadyExists_ThrowsValidationException() {
        // Arrange
        Authentication adminAuth = mock(Authentication.class);
        when(adminAuth.isAuthenticated()).thenReturn(true);
        
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(adminAuth);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

            // Act & Assert
            assertThrows(ValidationException.class, () -> {
                authService.register(registerRequest);
            });
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Test
    void register_NotAdmin_ThrowsAuthorizationException() {
        // Arrange
        Authentication socioAuth = mock(Authentication.class);
        when(socioAuth.isAuthenticated()).thenReturn(true);
        
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(socioAuth);

            // Act & Assert
            assertThrows(AuthorizationException.class, () -> {
                authService.register(registerRequest);
            });
        }
    }

    @Test
    void login_Success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("jwtToken");

        // Act
        String result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals("jwtToken", result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(authentication);
    }

    @Test
    void login_InvalidCredentials_ThrowsAuthenticationException() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void logout_WithBearerToken_Success() {
        // Arrange
        String authorizationHeader = "Bearer validToken";

        // Act
        String result = authService.logout(authorizationHeader);

        // Assert
        assertEquals("Logout successful. Token has been invalidated.", result);
        verify(jwtUtil).invalidateToken("validToken");
    }

    @Test
    void logout_WithoutBearerToken_Success() {
        // Arrange
        String authorizationHeader = "InvalidHeader";

        // Act
        String result = authService.logout(authorizationHeader);

        // Assert
        assertEquals("Logout successful. Please remove the token from client storage.", result);
        verify(jwtUtil, never()).invalidateToken(anyString());
    }
}
