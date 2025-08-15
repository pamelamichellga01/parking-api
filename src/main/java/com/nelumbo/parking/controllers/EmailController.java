package com.nelumbo.parking.controllers;

import com.nelumbo.parking.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    /**
     * Endpoint POST que recibe la solicitud de envío de correo
     * 
     * @param request DTO con email, placa, mensaje y parqueaderoNombre
     * @return Respuesta 200 con mensaje de confirmación
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail(@RequestBody EmailRequest request) {
        
        // Llamar al servicio de email
        boolean emailSent = emailService.sendEmail(
                request.getEmail(), 
                request.getPlaca(), 
                request.getMensaje(), 
                request.getParqueaderoNombre()
        );
        
        // Devolver respuesta 200 con el mensaje requerido
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Correo Enviado");
        
        return ResponseEntity.ok(response);
    }

    /**
     * DTO interno para recibir la solicitud de email
     */
    public static class EmailRequest {
        private String email;
        private String placa;
        private String mensaje;
        private String parqueaderoNombre;

        // Getters y Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPlaca() { return placa; }
        public void setPlaca(String placa) { this.placa = placa; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        
        public String getParqueaderoNombre() { return parqueaderoNombre; }
        public void setParqueaderoNombre(String parqueaderoNombre) { this.parqueaderoNombre = parqueaderoNombre; }
    }
}
