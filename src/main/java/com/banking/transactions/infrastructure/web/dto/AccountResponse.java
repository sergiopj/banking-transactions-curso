package com.banking.transactions.infrastructure.web.dto;

import java.util.List;

import com.banking.transactions.application.dto.AccountDetailsDto;

// DTO de respuesta que expone la API. No es entidad de dominio.
// Agrupa los datos de la cuenta y un resumen de sus movimientos.
public record AccountResponse(
        String id, // id de la cuenta (UUID como String)
        String customerId, // propietario de la cuenta
        double balance, // saldo actual - en producción mejor BigDecimal
        List<TransactionSummary> transactions) { // lista de movimientos resumidos

    // Factory method que convierte el DTO de application a DTO de web
    // Así la capa web no conoce entidades de persistencia
    public static AccountResponse fromDto(AccountDetailsDto dto) {
        // Mapeo de transacciones de application a transacciones de web
        List<TransactionSummary> transactions = dto.transactions().stream()
                .map(t -> new TransactionSummary(t.id(), t.transactionType(), t.amount()))
                .toList();

        return new AccountResponse(dto.id(), dto.customerId(), dto.balance(), transactions);
    }

    // DTO anidado para no crear otro fichero. Representa el resumen de una
    // transacción
    public record TransactionSummary(
            String id, // id de la transacción
            String transactionType, // tipo: DEPOSIT, WITHDRAWAL, TRANSFER
            double amount // importe del movimiento
    ) {
    }
}