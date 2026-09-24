package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;

/**
 * Puerto de Entrada - Query de lectura (CQRS). En application/port y no en
 * domain
 * porque domain solo conoce Account/Money, no DTOs. application orquesta.
 * Interface para DIP: Controller depende de abstracción, no del service
 * concreto -> testeable con mock.
 * getById(String) y no AccountId: el puerto recibe primitivo del @PathVariable,
 * la conversión a VO la hace el service.
 * Devuelve DTO y no Aggregate: no filtra dominio, devuelve Read Model listo
 * para JSON.
 */
public interface GetAccountDetailsUseCase {

    AccountDetailsDto getById(String accountId);
}