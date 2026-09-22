package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.WithdrawMoneyCommand;

/**
 * PUERTO DE ENTRADA (Port In) - Caso de Uso Retirar Dinero
 * 
 * PATH: application/port/ -> Contrato de lo que la aplicación sabe hacer.
 * El exterior (AccountController en infrastructure/web/) depende de esta
 * abstracción, no de la implementación. DIP de SOLID + Hexagonal.
 * 
 * POR QUÉ public interface: Es el API interno de tu aplicación. Tiene que ser
 * visible para que el adapter web lo inyecte y para que usecase/ lo implemente.
 * 
 * POR QUÉ WithdrawMoneyUseCase: Cada caso de uso tiene su propia interface
 * (ISP).
 * No haces una God interface BankingService con 10 métodos. Un UseCase = Una
 * responsabilidad.
 * 
 * POR QUÉ withdrawMoney(WithdrawMoneyCommand): Este sí viene bien escrito con
 * doble 'm'
 * a diferencia de DepositMoneyComand y CreateAccountComand. Recibe el COMMAND
 * inmutable
 * que trae accountId + amount desde el Controller. El UseCase es quien
 * convierte
 * String -> AccountId y double -> Money y valida.
 * 
 * POR QUÉ devuelve AccountDetailsDto: CQRS - después de escribir, devuelves
 * estado
 * actualizado para que el cliente vea el nuevo saldo. Pero nunca devuelves el
 * Agregado Account directamente, lo mapeas a DTO para no filtrar lógica de
 * dominio.
 */
public interface WithdrawMoneyUseCase {

    // Orquesta: findById -> valida saldo -> account.withdraw(Money) -> save() ->
    // MapToAccountDetailsDto.from()
    // Aquí es donde se puede lanzar InsufficientBalanceException
    AccountDetailsDto withdrawMoney(WithdrawMoneyCommand command);
}