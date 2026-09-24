package com.banking.transactions.infrastructure.mapper.AccountMapper;

import java.util.List;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.model.Transaction;
import com.banking.transactions.infrastructure.persistence.AccountEntity;
import com.banking.transactions.infrastructure.persistence.TransactionEntity;

/**
 * Mapper entre la capa de Infraestructura (JPA Entity) y el Dominio
 * (Aggregate).
 * Clase utilitaria estática — sin estado, sin @Component, sin Spring.
 * POR QUÉ static: Función pura. Entra entity, sale dominio. Sin new, sin estado
 * compartido.
 * POR QUÉ aquí y no en domain: Domain no conoce JPA. El mapper vive en infra,
 * que sí conoce ambos mundos.
 */
public final class AccountMapper {

    private AccountMapper() {
        // Previene instanciación
    }

    /**
     * Mapeo JPA Entity -> Dominio.
     * Usa Account.reconstitute() y Transaction.reconstitute() para reconstruir
     * el agregado desde la BD sin invocar lógica de negocio del constructor.
     */
    public static Account toDomain(AccountEntity entity) {
        List<Transaction> transactions = entity.getTransactions().stream()
                .map(AccountMapper::transactionToDomain)
                .toList();

        return Account.reconstitute(
                new AccountId(entity.getId()),
                entity.getCustomerId(),
                Money.from(entity.getBalance()),
                transactions);
    }

    // Helper: convierte cada TransactionEntity a Transaction de dominio
    private static Transaction transactionToDomain(TransactionEntity te) {
        return Transaction.reconstitute(
                te.getId(),
                te.getType(),
                Money.from(te.getAmount()),
                te.getCreatedAt());
    }

    // Mapeo DOMINIO -> JPA Entity
    public static AccountEntity toEntity(Account account) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(account.getId().value());
        accountEntity.setCustomerId(account.getCustomerId());
        accountEntity.setBalance(account.getBalance().getAmount());

        accountEntity.getTransactions().clear();

        for (Transaction t : account.getTransactions()) {
            TransactionEntity te = new TransactionEntity();
            te.setId(t.getId());
            te.setType(t.getTransactionType()); // enum directamente, no .name()
            te.setAmount(t.getAmount().getAmount());
            accountEntity.addTransaction(te);
        }

        return accountEntity;
    }

}
