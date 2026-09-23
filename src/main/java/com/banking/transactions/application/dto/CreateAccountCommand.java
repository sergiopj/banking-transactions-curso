package com.banking.transactions.application.dto;

/**
 * COMMAND DE ENTRADA - Para crear cuenta (Write Model CQRS)
 * 
 * PATH: application/dto/ -> Input que necesita CreateAccountUseCase.
 * No va en domain/ porque es solo transporte de datos, no tiene reglas.
 * No va en infrastructure/web/ para no atar el Core a Spring.
 * 
 * POR QUÉ public record: Command inmutable. Cuando pides crear cuenta con
 * cliente X y saldo Y, eso no se modifica a mitad del caso de uso.
 * 
 * POR QUÉ String customerId: Entra como String desde el JSON. El UseCase luego
 * valida si es necesario. No metes Value Objects aquí.
 * 
 * POR QUÉ Double (objeto) y no double (primitivo): Truco importante.
 * Double permite null. Así puedes crear cuenta sin saldo inicial (null -> 0)
 * o distinguir "no me mandó saldo" de "me mandó 0.0". Con double primitivo
 * siempre sería 0.0 y no sabrías. En banca esa diferencia importa.
 */
public record CreateAccountCommand(
                String customerId,
                Double initialBalance) {
}
