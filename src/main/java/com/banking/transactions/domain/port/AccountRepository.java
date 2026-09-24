package com.banking.transactions.domain.port;

import java.util.Optional;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;

/**
 * Puerto de Salida - Contrato que el DOMINIO exige a infraestructura (DIP).
 * Va en domain/port porque el dominio define lo que NECESITA (guardar/buscar),
 * no lo que ofrece JPA/Mongo/Memory. Core sin frameworks, sin @Repository.
 * Interface, no class: dice QUÉ, no CÓMO. Impl real en
 * infrastructure/persistence.
 * Usa VO AccountId para type-safety y Optional para forzar manejo de no
 * encontrado -> AccountNotFoundException.
 */
public interface AccountRepository {

    Account save(Account account); // guarda/actualiza el Aggregate completo

    Optional<Account> findById(AccountId accountId); // puede no existir
}