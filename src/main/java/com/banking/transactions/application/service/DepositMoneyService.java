package com.banking.transactions.application.service;

import org.springframework.stereotype.Service;
import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.DepositMoneyCommand;
import com.banking.transactions.application.dto.MapToAccountDetailsDto;
import com.banking.transactions.application.port.DepositMoneyUseCase;
import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.port.AccountRepository;
import jakarta.transaction.Transactional;

/**
 * Adapter del Caso de Uso Ingresar Dinero. Orquesta dominio, no sabe de HTTP ni
 * JPA.
 * 
 * @Service para inyección en Controller. Implementa puerto de entrada -> DIP +
 *          Hexagonal.
 *          Depende de AccountRepository (puerto de salida), no de JPA.
 */
@Service
public class DepositMoneyService implements DepositMoneyUseCase {

    private final AccountRepository accountRepository;

    public DepositMoneyService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // find + deposit + save atómico, si falla save hace rollback
    public AccountDetailsDto depositMoney(DepositMoneyCommand command) {
        // String del DTO -> VO de dominio que valida UUID
        var accountId = new AccountId(command.accountId());

        // El caso de uso decide si no existe: lanza excepción de dominio, no de Spring
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // double del Command -> String -> Money con BigDecimal exacto. deposit() crea
        // Transaction y actualiza balance
        account.deposit(Money.of(String.valueOf(command.amount())));

        accountRepository.save(account);

        // No filtras Aggregate, devuelves Read Model para JSON
        return MapToAccountDetailsDto.from(account);
    }
}