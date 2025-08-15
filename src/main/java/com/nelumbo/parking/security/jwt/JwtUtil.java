package com.nelumbo.parking.security.jwt;

import com.nelumbo.parking.entities.InvalidToken;
import com.nelumbo.parking.repositories.InvalidTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;
    private final InvalidTokenRepository invalidTokenRepository;

    public JwtUtil(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.expiration}") long expirationMs,
            InvalidTokenRepository invalidTokenRepository
    ) {
        // Decodifica Base64 -> bytes; valida fuerza internamente
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expirationMs = expirationMs;
        this.invalidTokenRepository = invalidTokenRepository;
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long now = System.currentTimeMillis();
        
        // Obtener los roles del usuario
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        
        return Jwts.builder()
                .subject(userDetails.getUsername())        // email
                .claim("roles", roles)                    // roles del usuario
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(key, Jwts.SIG.HS256)             // API 0.12.x
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        // Verificar si el token está en la blacklist
        if (isTokenInvalidated(token)) {
            return false;
        }
        
        final String email = extractUserName(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)   // verifica firma con la misma clave
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    // NUEVO MÉTODO: Extraer roles del token
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }
    
    public boolean isTokenInvalidated(String token) {
        return invalidTokenRepository.findByToken(token).isPresent();
    }
    
    public void invalidateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Date expiration = claims.getExpiration();
            
            InvalidToken invalidToken = InvalidToken.builder()
                    .token(token)
                    .invalidatedAt(LocalDateTime.now())
                    .expiresAt(expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .build();
            
            invalidTokenRepository.save(invalidToken);
        } catch (Exception e) {
            // Si hay error al procesar el token, lo ignoramos
        }
    }
    
    public void cleanupExpiredInvalidTokens() {
        invalidTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
