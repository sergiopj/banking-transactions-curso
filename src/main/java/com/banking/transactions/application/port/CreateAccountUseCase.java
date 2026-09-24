package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.CreateAccountCommand;

/**
 * Puerto de Entrada (Port In) - Define QUÉ hace la app, no CÓMO. Contrato que
 * ve el Controller.
 * Interface y no class para desacoplar: el adapter web solo conoce el puerto,
 * no el service concreto.
 * UseCase y no Service por DDD/Hexagonal: es lenguaje de negocio, no técnico de
 * Spring.
 * Recibe Command (Write Model) para tener 1 solo parámetro con intención. Si
 * añades campo, no cambias firma.
 * Devuelve DTO y no Account: Anti-Corruption Layer, no filtras el Aggregate con
 * Money/List fuera.
 */
public interface CreateAccountUseCase {

    /**
     * Valida Command -> crea Account (Root) -> save() ->
     * MapToAccountDetailsDto.from()
     */
    AccountDetailsDto createAccount(CreateAccountCommand command);
}