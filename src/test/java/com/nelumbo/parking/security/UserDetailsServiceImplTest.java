package com.nelumbo.parking.security;

import com.nelumbo.parking.entities.User;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        // Arrange
        String email = "user@mail.com";
        String encodedPassword = "$2a$10$hashhashhash";

        // Crea un User real (ajusta a tu entidad: builder/setters/constructor)
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .role(Role.ADMIN) // si tu User tiene Role
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDetails details = service.loadUserByUsername(email);

        // Assert
        assertNotNull(details);
        assertEquals(email, details.getUsername());
        assertEquals(encodedPassword, details.getPassword());

        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        String email = "noexist@mail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        var ex = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(email));

        assertTrue(ex.getMessage().contains("User not found with email: " + email));
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }
}
