package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.CreateAccountComand;

/**
 * PUERTO DE ENTRADA (Port In) - Caso de Uso Crear Cuenta
 * 
 * PATH: application/port/ -> Va aquí porque define QUÉ puede hacer la
 * aplicación,
 * no CÓMO. Es el contrato que el exterior (Controller) ve. Principio ISP de
 * SOLID.
 * 
 * POR QUÉ public interface y no class: Es el patrón Puerto y Adaptador. El
 * Controller
 * (adapter de entrada) solo conoce esta interface, no la implementación
 * concreta
 * que vive en application/usecase/CreateAccountUseCaseImpl. Así puedes cambiar
 * la
 * lógica sin tocar el web.
 * 
 * POR QUÉ UseCase y no Service: Semántica DDD/Hexagonal. Service es genérico y
 * de
 * Spring. UseCase dice "esta interface es UN caso de uso del negocio", no un
 * servicio técnico. Es lenguaje ubicuo.
 * 
 * POR QUÉ createAccount(CreateAccountComand): Recibe un COMMAND de entrada
 * (Write Model)
 * y no Account ni primitivos sueltos. Así el método tiene un solo parámetro con
 * intención clara. Si mañana añades currency, solo tocas el Command, no la
 * firma.
 * 
 * POR QUÉ devuelve AccountDetailsDto y no Account: Anti-Corruption Layer.
 * Nunca sacas el Agregado de dominio (Account con Money, List<Transaction>)
 * hacia fuera.
 * Devuelves un DTO de aplicación con primitivos listo para JSON. El dominio
 * queda protegido.
 */
public interface CreateAccountUseCase {

    // Orquesta la creación: valida Command -> crea Account -> save() ->
    // MapToAccountDetailsDto.from()
    AccountDetailsDto createAccount(CreateAccountComand command);
}