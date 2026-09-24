package com.banking.transactions.application.dto;

/**
 * Command de entrada para crear cuenta (Write Model). Lo consume
 * CreateAccountUseCase.
 * En application/dto para no atar el Core a Spring (infra) ni meter transporte
 * en domain.
 * Record inmutable: la petición no cambia a mitad del caso de uso.
 * customerId como String: entra del JSON, el UseCase valida luego.
 * Double objeto y no double primitivo: permite null para distinguir "sin saldo
 * inicial" de "saldo 0.0".
 */
public record CreateAccountCommand(
        String customerId,
        Double initialBalance) {
}