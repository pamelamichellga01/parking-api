package com.nelumbo.parking.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    /**
     * Simula el envío de un correo electrónico
     * 
     * @param email Email del destinatario
     * @param placa Placa del vehículo
     * @param mensaje Mensaje del correo
     * @param parqueaderoNombre Nombre del parqueadero
     * @return true si el email se envió correctamente
     */
    public boolean sendEmail(String email, String placa, String mensaje, String parqueaderoNombre) {
        try {
            // Imprimir en log la solicitud que llega
            log.info("=== SOLICITUD DE ENVÍO DE CORREO ===");
            log.info("Email: {}", email);
            log.info("Placa: {}", placa);
            log.info("Mensaje: {}", mensaje);
            log.info("Parqueadero: {}", parqueaderoNombre);
            log.info("=== FIN SOLICITUD ===");
            
            // Simular envío de email
            log.info("Simulando envío de correo a: {}", email);
            
            // Simular delay de envío
            Thread.sleep(100);
            
            log.info("Correo enviado exitosamente a: {}", email);
            return true;
            
        } catch (Exception e) {
            log.error("Error al enviar correo: {}", e.getMessage());
            return false;
        }
    }
}
