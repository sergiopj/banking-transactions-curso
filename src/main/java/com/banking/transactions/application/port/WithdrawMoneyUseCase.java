package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.WithdrawMoneyCommand;

/**
 * Puerto de Entrada - Caso Retirar Dinero. Contrato de lo que sabe hacer la
 * app.
 * En application/port para que Controller (infra) dependa de abstracción, no de
 * impl -> DIP + Hexagonal.
 * Una interface por caso de uso (ISP), no una God interface tipo
 * BankingService.
 * Recibe Command inmutable (accountId + amount) desde el Controller. El UseCase
 * convierte String->AccountId y double->Money.
 * Devuelve DTO y no Aggregate: tras escribir, retorna estado actualizado sin
 * filtrar dominio. Aquí puede salir InsufficientBalanceException.
 */
public interface WithdrawMoneyUseCase {

    /**
     * findById -> valida saldo -> account.withdraw(Money) -> save() ->
     * MapToAccountDetailsDto.from()
     */
    AccountDetailsDto withdrawMoney(WithdrawMoneyCommand command);
}