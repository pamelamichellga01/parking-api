package com.nelumbo.parking.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private final EmailService emailService = new EmailService();

    @Test
    void sendEmail_Success() {
        // Arrange
        String email = "test@example.com";
        String placa = "ABC123";
        String mensaje = "Test message";
        String parqueaderoNombre = "Test Parking";

        // Act
        boolean result = emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);

        // Assert
        assertTrue(result);
    }

    @Test
    void sendEmail_WithNullValues_Success() {
        // Arrange
        String email = null;
        String placa = null;
        String mensaje = null;
        String parqueaderoNombre = null;

        // Act
        boolean result = emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);

        // Assert
        assertTrue(result);
    }

    @Test
    void sendEmail_WithEmptyValues_Success() {
        // Arrange
        String email = "";
        String placa = "";
        String mensaje = "";
        String parqueaderoNombre = "";

        // Act
        boolean result = emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);

        // Assert
        assertTrue(result);
    }

    @Test
    void sendEmail_WithSpecialCharacters_Success() {
        // Arrange
        String email = "test+tag@example.com";
        String placa = "ABC-123";
        String mensaje = "Mensaje con acentos: áéíóú";
        String parqueaderoNombre = "Parqueadero Centro & Norte";

        // Act
        boolean result = emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);

        // Assert
        assertTrue(result);
    }

    @Test
    void sendEmail_WithLongValues_Success() {
        // Arrange
        String email = "very.long.email.address@very.long.domain.example.com";
        String placa = "ABC123";
        String mensaje = "Este es un mensaje muy largo que contiene mucha información " +
                "sobre el vehículo y el parqueadero. Debería ser procesado correctamente " +
                "sin importar su longitud.";
        String parqueaderoNombre = "Parqueadero con nombre muy largo y descriptivo";

        // Act
        boolean result = emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);

        // Assert
        assertTrue(result);
    }

    @Test
    void sendEmail_MultipleCalls_Success() {
        // Arrange
        String email1 = "test1@example.com";
        String email2 = "test2@example.com";
        String placa = "ABC123";
        String mensaje = "Test message";
        String parqueaderoNombre = "Test Parking";

        // Act
        boolean result1 = emailService.sendEmail(email1, placa, mensaje, parqueaderoNombre);
        boolean result2 = emailService.sendEmail(email2, placa, mensaje, parqueaderoNombre);

        // Assert
        assertTrue(result1);
        assertTrue(result2);
    }

    @Test
    void sendEmail_SimulatesDelay_Success() {
        // Arrange
        String email = "test@example.com";
        String placa = "ABC123";
        String mensaje = "Test message";
        String parqueaderoNombre = "Test Parking";

        long startTime = System.currentTimeMillis();

        // Act
        boolean result = emailService.sendEmail(email, placa, mensaje, parqueaderoNombre);
        long endTime = System.currentTimeMillis();

        // Assert
        assertTrue(result);
        // Verificar que hubo un delay mínimo (el servicio simula 100ms)
        assertTrue(endTime - startTime >= 50); // Permitimos un margen de error
    }
}
