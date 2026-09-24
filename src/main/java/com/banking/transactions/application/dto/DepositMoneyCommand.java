package com.banking.transactions.application.dto;

/**
 * Command de entrada para ingresar dinero. Lo consume DepositMoneyUseCase.
 * En application/dto para que el Core no dependa de Spring. El Controller
 * convierte su DepositRequest JSON a este Command.
 * Record inmutable: orden "ingresa X en cuenta Y" que no cambia.
 * accountId como String y no AccountId: entra del JSON/URL, el UseCase hace
 * AccountId.of() y valida.
 * amount como double y no Money: entra simple por JSON, el UseCase lo convierte
 * a Money.of() y valida negativos.
 */
public record DepositMoneyCommand(
        String accountId,
        double amount) {
}