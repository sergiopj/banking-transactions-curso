package com.banking.transactions.application.dto;

/**
 * DTO de lectura (Read Model). Lo genera MapToAccountDetailsDto desde el
 * Aggregate.
 * En application/dto porque es salida de la app, no va en domain (sin reglas)
 * ni en infra (sin JSON).
 * Record inmutable: historial que no cambia.
 * Aplana VOs a primitivos para el exterior: TransactionId -> String, enum ->
 * String, Money -> double.
 * Así no filtras dominio y el JSON queda plano: "DEPOSIT", 100.0
 */
public record TransactionDto(
                String id,
                String transactionType,
                double amount) {
}