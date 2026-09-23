package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;

/**
 * PUERTO DE ENTRADA - Query (CQRS lado lectura)
 * 
 * PATH: application/port/ -> Puerto de aplicación, no de dominio.
 * Si lo pusieras en domain/ obligarías al dominio a conocer AccountDetailsDto
 * que es un DTO. El dominio solo conoce Account, Money, AccountId.
 * application/ es quien orquesta y define cómo se usa el dominio.
 * 
 * POR QUÉ interface: Principio D de SOLID. infrastructure/web/AccountController
 * depende de esta abstracción, no de GetAccountDetailsService concreto.
 * Así puedes testear el controller con un mock sin levantar Spring.
 * 
 * POR QUÉ GetAccountDetailsUseCase: Nombre de intención de negocio, no técnico.
 * No es GetAccountService (Service es de Spring).
 * 
 * POR QUÉ getById(String accountId) y no (AccountId id):
 * El puerto es primitivo. El String viene del @PathVariable del controller.
 * La validación a Value Object AccountId la hace el adapter service.
 * Así el web no importa domain/model/.
 * 
 * POR QUÉ devuelve AccountDetailsDto y no Account: Nunca expones el Agregado.
 * Devuelves Read Model con String, double y List<TransactionDto> listo para
 * JSON.
 */
public interface GetAccountDetailsUseCase {

    AccountDetailsDto getById(String accountId);

}