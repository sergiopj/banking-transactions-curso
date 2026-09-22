package com.banking.transactions.application.dto;

import java.util.List;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.Transaction;

/**
 * CLASE MAPPER / TRADUCTOR (Anti-Corruption Layer)
 *
 * QUÉ HACE:
 * - Convierte el modelo de DOMINIO (Account con Money, AccountId,
 * List<Transaction>)
 * al DTO de APLICACIÓN (AccountDetailsDto con String y double) para devolverlo
 * al exterior.
 * - No contiene lógica de negocio; es un transformador de datos puro.
 *
 * DECISIONES DE DISEÑO:
 * - public final class:
 * Accesible desde Casos de Uso (application) y Controladores (infrastructure).
 * Es final para evitar herencia indeseada.
 * - private constructor:
 * Evita instanciación con 'new' (clase de utilidad estática).
 * - public static from(Account):
 * Función pura sin estado (entra Account, sale DTO). Evita asignaciones de
 * memoria
 * innecesarias por cada petición HTTP.
 * - private static mapTransaction(Transaction):
 * Helper interno que encapsula el mapeo individual de cada Transaction.
 * - Sin anotaciones de Spring (@Component / @Service):
 * Mantiene la capa de aplicación desacoplada del framework, siendo 100% Java
 * puro y testeable.
 */
public final class MapToAccountDetailsDto {

    private MapToAccountDetailsDto() {
        // Previene instanciación
    }

    // Convierte una entidad Account a su DTO de detalles
    public static AccountDetailsDto from(Account account) {
        List<TransactionDto> transactions = account.getTransactions().stream()
                .map(MapToAccountDetailsDto::mapTransaction)
                .toList();

        return new AccountDetailsDto(
                account.getId().value(),
                account.getCustomerId(),
                account.getBalance().getAmount().doubleValue(),
                transactions);
    }

    private static TransactionDto mapTransaction(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getTransactionType().name(),
                transaction.getAmount().getAmount().doubleValue());
    }

}