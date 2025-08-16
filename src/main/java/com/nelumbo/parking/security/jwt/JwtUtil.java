package com.nelumbo.parking.security.jwt;

import com.nelumbo.parking.entities.InvalidToken;
import com.nelumbo.parking.repositories.InvalidTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey key;
    private final long expirationMs;
    private final InvalidTokenRepository invalidTokenRepository;

    public JwtUtil(
            @Value("${app.jwt.secret}") String base64Secret,
            @Value("${app.jwt.expiration}") long expirationMs,
            InvalidTokenRepository invalidTokenRepository
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expirationMs = expirationMs;
        this.invalidTokenRepository = invalidTokenRepository;
    }

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        long now = System.currentTimeMillis();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expirationMs))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    // ÚNICA versión de validateToken (null-safe y manejando excepciones)
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            if (isTokenInvalidated(token)) {
                return false;
            }
            final String tokenSubject = extractUserName(token);
            return Objects.equals(userDetails.getUsername(), tokenSubject)
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false; // token malformado/firmado/expirado -> no válido
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // permite leer subject/exp aunque esté expirado
        }
    }

    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }

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
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("No se pudo invalidar el token (posiblemente malformado o ya expirado): {}", e.getMessage());
        }
    }

    public void cleanupExpiredInvalidTokens() {
        invalidTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
