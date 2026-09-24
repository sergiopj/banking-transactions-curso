package com.banking.transactions.infrastructure.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

// DTO inmutable para la creación de cuenta.
// Al ser un record, Java genera automáticamente el constructor con todos los parámetros,
// getters, equals, hashCode y toString. No lleva setters porque es inmutable.
public record CreateAccountRequest(

        // customerId del propietario. Valida que no venga null, vacío o solo espacios
        @NotBlank(message = "customerId cannot be blank") String customerId,

        // Balance inicial. Usamos BigDecimal para evitar errores de redondeo con
        // dinero.
        // Permite 0 pero no negativos
        @NotNull(message = "initialBalance is required") @PositiveOrZero(message = "initialBalance must be >= 0") BigDecimal initialBalance

) {
}