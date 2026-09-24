package com.banking.transactions.application.dto;

/**
 * Command de entrada para retirar dinero (Write Model). Input de
 * WithdrawMoneyUseCase.
 * En application/dto para que el Core no dependa de Spring. El Controller lo
 * crea desde el JSON.
 * Record inmutable: orden "retirar X de cuenta Y" que no cambia.
 * Command y no Request: Request es HTTP (infra), Command es intención de
 * escritura (app).
 * accountId como String y no AccountId: entra del borde como String, el UseCase
 * hace AccountId.of() y valida.
 * amount como double: entra simple por JSON, el UseCase lo convierte a Money
 * (BigDecimal) y valida.
 */
public record WithdrawMoneyCommand(
                String accountId,
                double amount) {
}