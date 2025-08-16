package com.nelumbo.parking.controllers;

import com.nelumbo.parking.services.EmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @Test
    void sendEmail_AlwaysReturnsSuccess() {
        // Act
        ResponseEntity<Map<String, String>> response = emailController.sendEmail();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Correo Enviado", response.getBody().get("mensaje"));
        // El controlador no llama al servicio, solo retorna un mensaje fijo
    }

    @Test
    void sendEmail_ResponseStructure() {
        // Act
        ResponseEntity<Map<String, String>> response = emailController.sendEmail();

        // Assert
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("mensaje"));
        assertEquals("Correo Enviado", response.getBody().get("mensaje"));
    }

    @Test
    void sendEmail_NoServiceDependency() {
        // Act
        ResponseEntity<Map<String, String>> response = emailController.sendEmail();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verificar que no se llama al servicio
        verifyNoInteractions(emailService);
    }

    @Test
    void sendEmail_ResponseType() {
        // Act
        ResponseEntity<Map<String, String>> response = emailController.sendEmail();

        // Assert
        assertNotNull(response);
        assertTrue(response instanceof ResponseEntity);
        assertEquals(Map.class, response.getBody().getClass());
    }

    @Test
    void sendEmail_MessageContent() {
        // Act
        ResponseEntity<Map<String, String>> response = emailController.sendEmail();

        // Assert
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Correo Enviado", body.get("mensaje"));
        assertEquals(1, body.size()); // Solo debe tener un campo
    }

    @Test
    void sendEmail_MultipleCallsConsistent() {
        // Act
        ResponseEntity<Map<String, String>> response1 = emailController.sendEmail();
        ResponseEntity<Map<String, String>> response2 = emailController.sendEmail();
        ResponseEntity<Map<String, String>> response3 = emailController.sendEmail();

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());
        
        assertEquals("Correo Enviado", response1.getBody().get("mensaje"));
        assertEquals("Correo Enviado", response2.getBody().get("mensaje"));
        assertEquals("Correo Enviado", response3.getBody().get("mensaje"));
        
        // Verificar que no se llama al servicio en ninguna ocasión
        verifyNoInteractions(emailService);
    }
}
