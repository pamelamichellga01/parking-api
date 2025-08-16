package com.nelumbo.parking.controllers;

import com.nelumbo.parking.dto.LoginRequest;
import com.nelumbo.parking.dto.RegisterRequest;
import com.nelumbo.parking.enums.Role;
import com.nelumbo.parking.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.SOCIO);
    }

    @org.junit.jupiter.params.ParameterizedTest(name = "[{index}] login con {2}")
    @org.junit.jupiter.params.provider.MethodSource("loginCases")
    void login_VariousCredentials_Success(LoginRequest req, String expectedToken) {
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedToken);

        ResponseEntity<?> response = authController.login(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(authService).login(req);
    }

    private static java.util.stream.Stream<org.junit.jupiter.params.provider.Arguments> loginCases() {
        LoginRequest normal = new LoginRequest();
        normal.setEmail("test@example.com");
        normal.setPassword("password123");

        LoginRequest different = new LoginRequest();
        different.setEmail("admin@example.com");
        different.setPassword("admin123");

        LoginRequest empty = new LoginRequest();
        empty.setEmail("");
        empty.setPassword("");

        LoginRequest nul = new LoginRequest();
        nul.setEmail(null);
        nul.setPassword(null);

        return java.util.stream.Stream.of(
                org.junit.jupiter.params.provider.Arguments.arguments(normal,   "jwt.token.here",          "credenciales válidas"),
                org.junit.jupiter.params.provider.Arguments.arguments(different,"admin.jwt.token",         "credenciales distintas"),
                org.junit.jupiter.params.provider.Arguments.arguments(empty,    "empty.credentials.token", "credenciales vacías"),
                org.junit.jupiter.params.provider.Arguments.arguments(nul,      "null.credentials.token",  "credenciales nulas")
        );
    }


    @Test
    void login_Success() {
        // Arrange
        String expectedToken = "jwt.token.here";
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedToken);

        // Act
        ResponseEntity<?> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(authService).login(loginRequest);
    }

    @Test
    void login_WithDifferentCredentials_Success() {
        // Arrange
        LoginRequest differentRequest = new LoginRequest();
        differentRequest.setEmail("admin@example.com");
        differentRequest.setPassword("admin123");

        String expectedToken = "admin.jwt.token";
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedToken);

        // Act
        ResponseEntity<?> response = authController.login(differentRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(authService).login(differentRequest);
    }

    @Test
    void login_WithEmptyCredentials_Success() {
        // Arrange
        LoginRequest emptyRequest = new LoginRequest();
        emptyRequest.setEmail("");
        emptyRequest.setPassword("");

        String expectedToken = "empty.credentials.token";
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedToken);

        // Act
        ResponseEntity<?> response = authController.login(emptyRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(authService).login(emptyRequest);
    }

    @Test
    void login_WithNullCredentials_Success() {
        // Arrange
        LoginRequest nullRequest = new LoginRequest();
        nullRequest.setEmail(null);
        nullRequest.setPassword(null);

        String expectedToken = "null.credentials.token";
        when(authService.login(any(LoginRequest.class))).thenReturn(expectedToken);

        // Act
        ResponseEntity<?> response = authController.login(nullRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, response.getBody());
        verify(authService).login(nullRequest);
    }

    @Test
    void register_Success() {
        // Arrange
        String expectedMessage = "User registered successfully by administrator";
        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedMessage);

        // Act
        ResponseEntity<?> response = authController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
        verify(authService).register(registerRequest);
    }

    @Test
    void register_WithDifferentRole_Success() {
        // Arrange
        RegisterRequest adminRequest = new RegisterRequest();
        adminRequest.setName("Admin User");
        adminRequest.setEmail("admin@example.com");
        adminRequest.setPassword("admin123");
        adminRequest.setRole(Role.ADMIN);

        String expectedMessage = "Admin user registered successfully";
        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedMessage);

        // Act
        ResponseEntity<?> response = authController.register(adminRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
        verify(authService).register(adminRequest);
    }

    @Test
    void register_WithEmptyFields_Success() {
        // Arrange
        RegisterRequest emptyRequest = new RegisterRequest();
        emptyRequest.setName("");
        emptyRequest.setEmail("");
        emptyRequest.setPassword("");
        emptyRequest.setRole(null);

        String expectedMessage = "Empty user registered";
        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedMessage);

        // Act
        ResponseEntity<?> response = authController.register(emptyRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
        verify(authService).register(emptyRequest);
    }

    @Test
    void register_WithNullFields_Success() {
        // Arrange
        RegisterRequest nullRequest = new RegisterRequest();
        nullRequest.setName(null);
        nullRequest.setEmail(null);
        nullRequest.setPassword(null);
        nullRequest.setRole(null);

        String expectedMessage = "Null user registered";
        when(authService.register(any(RegisterRequest.class))).thenReturn(expectedMessage);

        // Act
        ResponseEntity<?> response = authController.register(nullRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
        verify(authService).register(nullRequest);
    }

    @Test
    void logout_WithNullHeader_Success() {
        // Arrange
        String expectedMessage = "Logout successful. Please remove the token from client storage.";
        when(authService.logout(null)).thenReturn(expectedMessage);

        // Act
        ResponseEntity<?> response = authController.logout(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedMessage, response.getBody());
        verify(authService).logout(null);
    }

    @Test
    void logout_WithDifferentTokenFormats_Success() {
        // Arrange
        String[] headers = {
            "Bearer token.here",
            "BEARER different.token",
            "bearer lowercase.token",
            "TokenWithoutBearer",
            "Bearer",
            "Bearer "
        };

        String expectedMessage = "Logout successful";
        when(authService.logout(anyString())).thenReturn(expectedMessage);

        // Act & Assert
        for (String header : headers) {
            ResponseEntity<?> response = authController.logout(header);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedMessage, response.getBody());
        }

        verify(authService, times(headers.length)).logout(anyString());
    }

    @Test
    void loginRequest_ConstructorAndGetters_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        String email = "test@example.com";
        String password = "password123";

        // Act
        request.setEmail(email);
        request.setPassword(password);

        // Assert
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
    }

    @Test
    void loginRequest_SettersAndGetters_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        String originalEmail = "original@example.com";
        String newEmail = "new@example.com";

        // Act
        request.setEmail(originalEmail);
        assertEquals(originalEmail, request.getEmail());

        request.setEmail(newEmail);

        // Assert
        assertEquals(newEmail, request.getEmail());
    }

    @Test
    void registerRequest_ConstructorAndGetters_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        String name = "Test User";
        String email = "test@example.com";
        String password = "password123";
        Role role = Role.SOCIO;

        // Act
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setRole(role);

        // Assert
        assertEquals(name, request.getName());
        assertEquals(email, request.getEmail());
        assertEquals(password, request.getPassword());
        assertEquals(role, request.getRole());
    }

    @Test
    void registerRequest_SettersAndGetters_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        String originalName = "Original Name";
        String newName = "New Name";

        // Act
        request.setName(originalName);
        assertEquals(originalName, request.getName());

        request.setName(newName);

        // Assert
        assertEquals(newName, request.getName());
    }
}
