package com.nelumbo.parking.config;

import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@DependsOn("userRepository")
public class DataLoader implements CommandLineRunner {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@mail.com").isEmpty()) {
            User admin = User.builder()
                    .name("Administrator")
                    .email("admin@mail.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Usuario admin creado");
        }
    }
}
