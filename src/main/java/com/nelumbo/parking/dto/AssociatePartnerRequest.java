package com.nelumbo.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociatePartnerRequest {
    
    @NotNull(message = "El ID del socio es obligatorio")
    private Long partnerId;
}
