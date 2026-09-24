package com.banking.transactions.infrastructure.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.port.AccountRepository;

/**
 * Adapter de salida In-Memory. Implementa el puerto de dominio
 * AccountRepository.
 * Simula la BD con un Map para poder probar los casos de uso sin MySQL/JPA.
 * En prod se sustituirá por JpaAccountRepositoryAdapter.
 */
@Repository // Lo detecta Spring como bean del puerto AccountRepository
public class InMemoryAccountRepository implements AccountRepository {

    /**
     * BD falsa en memoria. ConcurrentHashMap para ser thread-safe si hay retiros
     * concurrentes.
     */
    private final Map<AccountId, Account> database = new ConcurrentHashMap<>();

    /**
     * Guarda/actualiza el Aggregate completo (Account + Transactions). Es el límite
     * de consistencia: todo junto.
     */
    @Override
    public Account save(Account account) {
        database.put(account.getId(), account);
        return account;
    }

    /**
     * Busca el Root Aggregate por su VO AccountId. Devuelve Optional porque puede
     * no existir.
     */
    @Override
    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(database.get(accountId));
    }
}