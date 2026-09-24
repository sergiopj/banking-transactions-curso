package com.banking.transactions.application.service;

import org.springframework.stereotype.Service;
import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.MapToAccountDetailsDto;
import com.banking.transactions.application.dto.WithdrawMoneyCommand;
import com.banking.transactions.application.port.WithdrawMoneyUseCase;
import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.port.AccountRepository;
import jakarta.transaction.Transactional;

/**
 * Adapter Caso de Uso Retirar Dinero. Capa app: DTO in -> Agregado -> save ->
 * DTO out.
 * No sabe de JSON ni tablas. @Service para Controller. Implementa puerto de
 * entrada -> DIP.
 * Depende de AccountRepository (puerto de salida), no de JPA/Mongo.
 */
@Service
public class WithdrawMoneyService implements WithdrawMoneyUseCase {

    private final AccountRepository accountRepository;

    public WithdrawMoneyService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // find + withdraw + save atómico, rollback si falla regla de negocio
    public AccountDetailsDto withdrawMoney(WithdrawMoneyCommand command) {
        var accountId = new AccountId(command.accountId()); // String -> VO que valida UUID

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId)); // 404 si no existe

        account.withdraw(Money.of(String.valueOf(command.amount()))); // delega a dominio, valida saldo ->
                                                                      // InsufficientBalance. double->String->BigDecimal
                                                                      // exacto

        accountRepository.save(account); // persiste nuevo balance + Transaction interna

        return MapToAccountDetailsDto.from(account); // Aggregate -> DTO, no filtras dominio
    }
}