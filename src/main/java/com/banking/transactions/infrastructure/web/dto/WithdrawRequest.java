package com.banking.transactions.infrastructure.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// DTO para la operación de retiro. Solo necesita el importe a retirar.
// Record inmutable, el constructor y getter se generan automáticamente.
public record WithdrawRequest(

        // Importe a retirar. Debe venir informado y ser mayor que 0
        @NotNull(message = "amount is required") @Positive(message = "amount must be > 0") BigDecimal amount

) {
}