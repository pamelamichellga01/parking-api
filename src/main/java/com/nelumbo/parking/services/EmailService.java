package com.nelumbo.parking.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    public boolean sendEmail(String email, String placa, String mensaje, String parqueaderoNombre) {
        log.info("=== SOLICITUD DE ENVÍO DE CORREO ===");
        log.info("Email: {}", email);
        log.info("Placa: {}", placa);
        log.info("Mensaje: {}", mensaje);
        log.info("Parqueadero: {}", parqueaderoNombre);
        log.info("=== FIN SOLICITUD ===");

        try {
            log.info("Simulando envío de correo a: {}", email);
            Thread.sleep(100);
            log.info("Correo enviado exitosamente a: {}", email);
            return true;

        } catch (InterruptedException ie) {

            Thread.currentThread().interrupt();
            log.warn("Envío de correo interrumpido para {}.", email, ie);
            return false;
        } catch (RuntimeException e) {

            log.error("Error al enviar correo a {}: {}", email, e.getMessage(), e);
            return false;
        }
    }
}