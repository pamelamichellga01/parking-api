package com.nelumbo.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingRequest {

    @NotBlank(message = "El nombre del parqueadero es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 1000, message = "La capacidad no puede exceder 1000")
    private Integer capacity;

    @NotNull(message = "El costo por hora es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo por hora debe ser mayor a 0")
    @Digits(integer = 5, fraction = 2, message = "El costo por hora debe tener máximo 5 dígitos enteros y 2 decimales")
    private BigDecimal hourlyRate;

    // El socio es OPCIONAL - no es obligatorio
    private Long partnerId;
}
