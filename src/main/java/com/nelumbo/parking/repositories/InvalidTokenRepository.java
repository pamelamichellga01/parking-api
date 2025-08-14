package com.nelumbo.parking.repositories;

import com.nelumbo.parking.entities.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, Long> {
    
    Optional<InvalidToken> findByToken(String token);
    
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
