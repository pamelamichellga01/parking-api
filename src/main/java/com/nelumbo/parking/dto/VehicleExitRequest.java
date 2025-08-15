package com.nelumbo.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleExitRequest {
    
    @NotBlank(message = "La placa es obligatoria")
    @Pattern(regexp = "^[A-Z0-9]{6}$", message = "La placa debe tener exactamente 6 caracteres alfanuméricos en mayúsculas")
    private String licensePlate;
    
    @NotNull(message = "El ID del parqueadero es obligatorio")
    private Long parkingId;
}
