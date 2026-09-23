package com.banking.transactions.infrastructure.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.port.AccountRepository;

/**
 * ADAPTADOR DE SALIDA EN MEMORIA (InMemory Output Adapter)
 * 
 * Implementa el puerto de dominio AccountRepository.
 * Permite a la aplicación arrancar y ejecutar los Casos de Uso sin depender
 * todavía de la base de datos MySQL/JPA.
 */
@Repository
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<AccountId, Account> database = new ConcurrentHashMap<>();

    @Override
    public Account save(Account account) {
        database.put(account.getId(), account);
        return account;
    }

    @Override
    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(database.get(accountId));
    }

}
