package com.banking.transactions.application.service;

import org.springframework.stereotype.Service;
import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.CreateAccountCommand;
import com.banking.transactions.application.dto.MapToAccountDetailsDto;
import com.banking.transactions.application.port.CreateAccountUseCase;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.port.AccountRepository;
import jakarta.transaction.Transactional;

/**
 * Adapter del Caso de Uso Crear Cuenta. Orquesta dominio.
 * En application/service y no en infra porque es lógica de app, no detalle
 * técnico.
 * 
 * @Service para que Spring lo detecte e inyecte en el Controller.
 *          Implementa CreateAccountUseCase (puerto de entrada) -> DIP: web solo
 *          conoce interface.
 *          Depende de AccountRepository (puerto de salida), no de JPA; impl
 *          real en infra/persistence.
 */
@Service
public class CreateAccountService implements CreateAccountUseCase {

    private final AccountRepository accountRepository;

    public CreateAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository; // constructor injection: inmutable y testeable
    }

    @Override
    @Transactional // rollback si save() falla
    public AccountDetailsDto createAccount(CreateAccountCommand command) {
        // Double nullable: null = no mandó saldo -> Money.zero(), si no, String.valueOf
        // para BigDecimal exacto
        Money initialBalance = (command.initialBalance() == null)
                ? Money.zero()
                : Money.of(String.valueOf(command.initialBalance()));

        // Aggregate genera su identidad, no la DB. Constructor ya valida estado válido
        Account account = new Account(
                AccountId.newId(),
                command.customerId(),
                initialBalance);

        accountRepository.save(account);

        // Anti-Corruption: no filtras Aggregate, devuelves DTO de lectura listo para
        // JSON
        return MapToAccountDetailsDto.from(account);
    }
}