package com.banking.transactions.application.port;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.DepositMoneyCommand;

/**
 * PUERTO DE ENTRADA (Port In) - Caso de Uso Ingresar Dinero
 * 
 * PATH: application/port/ -> Define el contrato que la app ofrece al exterior.
 * El Controller (infrastructure/web/) solo conoce esta interface, no la clase
 * concreta de application/usecase/. Inversión de Dependencias.
 * 
 * POR QUÉ public interface: Para que pueda ser implementada por
 * DepositMoneyUseCaseImpl y consumida por AccountController. Si fuera
 * package-private, el adapter web no la vería.
 * 
 * POR QUÉ DepositMoneyUseCase y no DepositService: UseCase = lenguaje de
 * negocio.
 * Service es de Spring. En Hexagonal cada interface es UN caso de uso del
 * sistema
 * bancario, no un servicio técnico genérico.
 * 
 * POR QUÉ depositMoney(DepositMoneyComand): Recibe un COMMAND con intención.
 * No recibe (String accountId, double amount) sueltos. Si recibiera primitivos,
 * la firma se vuelve frágil. Con Command encapsulas y puedes añadir validación.
 * 
 * POR QUÉ devuelve AccountDetailsDto: Misma regla de siempre, nunca devuelves
 * el
 * Agregado Account (con Money, List<Transaction>) al exterior. Devuelves DTO
 * de lectura con el saldo actualizado y la transacción creada.
 */
public interface DepositMoneyUseCase {

    // Orquesta: findById -> Money.of(command.amount()) -> account.deposit() ->
    // save() -> MapToAccountDetailsDto.from()
    AccountDetailsDto depositMoney(DepositMoneyCommand command);
}