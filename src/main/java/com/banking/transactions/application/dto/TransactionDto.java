package com.banking.transactions.application.dto;

/**
 * DTO DE LECTURA - Item del historial (Read Model CQRS)
 * 
 * PATH: application/dto/ -> Va aquí porque es lo que DEVUELVE la aplicación.
 * Lo usa MapToAccountDetailsDto.from(account) para transformar
 * domain/model/Transaction -> TransactionDto. No va en domain/ porque
 * es solo transporte, no tiene reglas. No va en infrastructure/web/ porque
 * el Core no debe depender de JSON.
 * 
 * POR QUÉ public record: Dato inmutable. Una transacción ya ocurrida no cambia.
 * Te da constructor + id() / transactionType() / amount() gratis sin getters.
 * 
 * POR QUÉ TransactionDto y no TransactionResponse: Response es palabra de HTTP
 * (infrastructure). Dto es palabra de Aplicación. Este DTO puede usarse tanto
 * para la API REST como para otro adapter (ej. mensajería) sin atarse a Spring.
 * 
 * POR QUÉ String id y no TransactionId: En el borde de salida expones String.
 * El dominio usa TransactionId Value Object, pero hacia fuera lo aplanas a
 * primitivo para JSON. Anti-Corruption Layer.
 * 
 * POR QUÉ String transactionType y no enum TransactionType: Misma razón.
 * En domain tienes enum DEPOSIT / WITHDRAWAL. Aquí lo serializas a String
 * "DEPOSIT" para no filtrar el enum del dominio al exterior.
 * 
 * POR QUÉ double amount y no Money: Para el JSON. El cliente espera 100.0, no
 * { "amount": { "value": "100.00", "currency": "EUR" } }. El UseCase hace
 * transaction.getMoney().getAmount().doubleValue() o toString() -> double.
 */
public record TransactionDto(
        String id,
        String transactionType,
        double amount) {
}