package com.nelumbo.parking.controllers;

import com.nelumbo.parking.services.EmailService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendEmail() {
        return ResponseEntity.ok(Map.of("mensaje", "Correo Enviado"));
    }
}
