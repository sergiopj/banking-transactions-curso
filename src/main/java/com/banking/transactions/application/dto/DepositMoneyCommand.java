package com.banking.transactions.application.dto;

/**
 * COMMAND DE ENTRADA - Para ingresar dinero (Write Model CQRS)
 * 
 * PATH: application/dto/ -> Input que necesita DepositMoneyUseCase.
 * Va aquí y no en infrastructure/web/ para que el Core no dependa de Spring.
 * El Controller convierte su DepositRequest (JSON) a este Command.
 * 
 * POR QUÉ public record: Command inmutable. Es una orden: "ingresa X en cuenta
 * Y".
 * Esa orden no cambia una vez creada. record te da inmutabilidad + constructor
 * + getters accountId() / amount() sin boilerplate.
 * 
 * POR QUÉ DepositMoneyComand con typo: Tu repo lo tiene sin la segunda 'm'.
 * Lo dejo así para que te compile. El correcto sería DepositMoneyCommand.
 * 
 * POR QUÉ String accountId y no AccountId: En el borde entra como String desde
 * el JSON/URL. El UseCase luego hace AccountId.of(accountId) y valida.
 * Si pusieras AccountId aquí, meterías el dominio en el DTO de entrada.
 * 
 * POR QUÉ double amount y no Money: Mismo patrón del curso. Entra como double
 * por simplicidad de JSON y el UseCase lo convierte a Money.of(amount) donde
 * ya se valida que no sea negativo con NegativeMoneyException.
 */
public record DepositMoneyCommand(
                String accountId,
                double amount) {
}