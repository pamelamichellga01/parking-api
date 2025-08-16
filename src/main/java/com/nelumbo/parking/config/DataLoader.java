package com.nelumbo.parking.config;

import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("userRepository")
public class DataLoader implements CommandLineRunner {

    @Value("${dataloader.user.pilot.password}")
    private String password;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail("admin@mail.com").isEmpty()) {
            User admin = User.builder()
                    .name("Administrator")
                    .email("admin@mail.com")
                    .password(passwordEncoder.encode(password))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("✅ Usuario admin creado");

        }
    }
}
