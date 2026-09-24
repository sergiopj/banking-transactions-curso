package com.banking.transactions.application.dto;

import java.util.List;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.Transaction;

/**
 * Mapper / Anti-Corruption Layer. Traduce DOMINIO (Account con Money,
 * AccountId)
 * a APLICACIÓN (AccountDetailsDto con String/double). Sin lógica de negocio.
 * final para no heredar, constructor privado para no instanciar (utilidad
 * estática).
 * Métodos static puros sin estado, sin @Component para no atar a Spring y ser
 * 100% testeable.
 */
public final class MapToAccountDetailsDto {

    private MapToAccountDetailsDto() {
    } // utilidad estática

    /** Account (Aggregate Root) -> AccountDetailsDto */
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

    /** Transaction (Entidad Hija) -> TransactionDto */
    private static TransactionDto mapTransaction(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getTransactionType().name(),
                transaction.getAmount().getAmount().doubleValue());
    }
}