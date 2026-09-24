package com.banking.transactions.infrastructure.web.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// DTO para la operación de depósito. Solo necesita el importe.
// Es un record, así que el constructor y el getter se generan solos.
public record DepositRequest(

        // Importe a depositar. BigDecimal para no perder precisión con dinero.
        // Debe venir informado y ser mayor que 0
        @NotNull(message = "amount is required") @Positive(message = "amount must be > 0") BigDecimal amount

) {
}