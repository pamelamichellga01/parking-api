package com.nelumbo.parking.config;

import com.nelumbo.parking.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final JwtUtil jwtUtil;

    // Ejecutar cada día a las 2:00 AM (configurable)
    @Scheduled(cron = "${app.scheduler.token-cleanup.cron:0 0 2 * * ?}")
    public void cleanupExpiredTokens() {
        jwtUtil.cleanupExpiredInvalidTokens();
    }
}
