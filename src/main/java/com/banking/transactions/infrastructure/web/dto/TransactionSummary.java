package com.banking.transactions.infrastructure.web.dto;

import java.math.BigDecimal;

// Resumen de la transacción que se devuelve tras cada operación de depósito o retiro
// Contiene el identificador, el tipo de transacción y el importe movido
public record TransactionSummary(
        String id, // id de la transacción (UUID)
        String transactionType, // tipo: DEPOSIT, WITHDRAWAL, TRANSFER - en minúscula para que el JSON sea
                                // transactionType
        BigDecimal amount // importe - BigDecimal para no perder precisión con dinero
) {
}