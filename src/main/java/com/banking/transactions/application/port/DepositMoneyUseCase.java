package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.DepositMoneyCommand;

/**
 * Puerto de Entrada - Caso Ingresar Dinero. Contrato que la app expone al
 * Controller.
 * Interface y no clase: el adapter web solo ve el puerto, no la impl concreta
 * -> DIP.
 * UseCase y no Service: lenguaje de negocio hexagonal, no técnico de Spring.
 * Recibe Command con intención y no primitivos sueltos, así la firma no es
 * frágil.
 * Devuelve DTO y no Aggregate: Anti-Corruption, no filtra Account con
 * Money/List.
 */
public interface DepositMoneyUseCase {

    /**
     * findById -> Money.of(amount) -> account.deposit() -> save() ->
     * MapToAccountDetailsDto.from()
     */
    AccountDetailsDto depositMoney(DepositMoneyCommand command);
}