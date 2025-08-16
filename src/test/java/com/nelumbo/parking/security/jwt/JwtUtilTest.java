package com.nelumbo.parking.security.jwt;

import com.nelumbo.parking.entities.InvalidToken;
import com.nelumbo.parking.repositories.InvalidTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtUtilTest {

    @MockitoBean
    private InvalidTokenRepository invalidTokenRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Authentication mockAuthentication;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        // Mock de Authentication
        mockAuthentication = mock(Authentication.class);
        mockUserDetails = mock(UserDetails.class);

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
        when(mockUserDetails.getUsername()).thenReturn("test@example.com");
        
        // Usar Collections.emptyList() para evitar problemas de tipos genéricos
        lenient().when(mockAuthentication.getAuthorities()).thenReturn(Collections.emptyList());
    }

    @Test
    void generateToken_Success() {
        // Act
        String token = jwtUtil.generateToken(mockAuthentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length, "Un JWT debe tener 3 partes");
    }

    @Test
    void generateToken_ContainsUserEmail() {
        // Act
        String token = jwtUtil.generateToken(mockAuthentication);

        // Assert
        String extractedEmail = jwtUtil.extractUserName(token);
        assertEquals("test@example.com", extractedEmail);
    }

    @Test
    void generateToken_ContainsRoles() {
        // Act
        String token = jwtUtil.generateToken(mockAuthentication);

        // Assert
        List<String> roles = jwtUtil.extractRoles(token);
        assertNotNull(roles);
        assertEquals(0, roles.size()); // Sin roles en el mock
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Act
        String token = jwtUtil.generateToken(mockAuthentication);

        // Assert
        boolean isValid = jwtUtil.validateToken(token, mockUserDetails);
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidUser_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);
        UserDetails differentUser = mock(UserDetails.class);
        when(differentUser.getUsername()).thenReturn("different@example.com");

        // Act
        boolean isValid = jwtUtil.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Arrange — JwtUtil con expiración ya vencida
        String secret = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmYTA=";
        long expiredMs = -1000L; // expirado al momento de generarse
        JwtUtil expiredJwtUtil = new JwtUtil(secret, expiredMs, invalidTokenRepository);

        String token = expiredJwtUtil.generateToken(mockAuthentication);

        // Act
        boolean isValid = expiredJwtUtil.validateToken(token, mockUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_InvalidatedToken_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);
        when(invalidTokenRepository.findByToken(token)).thenReturn(Optional.of(new InvalidToken()));

        // Act
        boolean isValid = jwtUtil.validateToken(token, mockUserDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void extractExpiration_Success() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);

        // Act
        Date expiration = jwtUtil.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // Debe estar en el futuro
    }

    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);

        // Act
        boolean isExpired = jwtUtil.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_ExpiredToken_ReturnsTrue() {
        // Arrange — JwtUtil con expiración ya vencida
        String secret = "ZmQ0ZGI5NjQ0MDQwY2I4MjMxY2Y3ZmI3MjdhN2ZmYTA=";
        long expiredMs = -1000L; // expirado al momento de generarse
        JwtUtil expiredJwtUtil = new JwtUtil(secret, expiredMs, invalidTokenRepository);

        String token = expiredJwtUtil.generateToken(mockAuthentication);

        // Act
        boolean isExpired = expiredJwtUtil.isTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    void isTokenInvalidated_ValidToken_ReturnsFalse() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);
        when(invalidTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Act
        boolean isInvalidated = jwtUtil.isTokenInvalidated(token);

        // Assert
        assertFalse(isInvalidated);
    }

    @Test
    void isTokenInvalidated_InvalidatedToken_ReturnsTrue() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);
        when(invalidTokenRepository.findByToken(token)).thenReturn(Optional.of(new InvalidToken()));

        // Act
        boolean isInvalidated = jwtUtil.isTokenInvalidated(token);

        // Assert
        assertTrue(isInvalidated);
    }

    @Test
    void invalidateToken_Success() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);
        when(invalidTokenRepository.save(any(InvalidToken.class))).thenReturn(new InvalidToken());

        // Act
        assertDoesNotThrow(() -> jwtUtil.invalidateToken(token));

        // Assert
        verify(invalidTokenRepository).save(any(InvalidToken.class));
    }

    @Test
    void invalidateToken_InvalidToken_HandlesException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertDoesNotThrow(() -> jwtUtil.invalidateToken(invalidToken));
    }

    @Test
    void cleanupExpiredInvalidTokens_Success() {
        // Act
        assertDoesNotThrow(() -> jwtUtil.cleanupExpiredInvalidTokens());

        // Assert
        verify(invalidTokenRepository).deleteByExpiresAtBefore(any(LocalDateTime.class));
    }

    @Test
    void extractAllClaims_Success() {
        // Arrange
        String token = jwtUtil.generateToken(mockAuthentication);

        // Act
        assertDoesNotThrow(() -> jwtUtil.extractAllClaims(token));
        // Si no lanza excepción, el método funciona correctamente
    }

    @Test
    void generateToken_WithNoRoles_Success() {
        // Arrange - ya configurado en setUp() con Collections.emptyList()

        // Act
        String token = jwtUtil.generateToken(mockAuthentication);

        // Assert
        assertNotNull(token);
        List<String> roles = jwtUtil.extractRoles(token);
        assertEquals(0, roles.size());
    }
}
