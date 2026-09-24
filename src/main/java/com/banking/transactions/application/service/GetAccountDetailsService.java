package com.banking.transactions.application.service;

import org.springframework.stereotype.Service;
import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.MapToAccountDetailsDto;
import com.banking.transactions.application.port.GetAccountDetailsUseCase;
import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.port.AccountRepository;
import jakarta.transaction.Transactional;

/**
 * Adapter Query - lado Q de CQRS, solo lee. Create/Deposit/Withdraw son el lado
 * C.
 * 
 * @Service inyectado en Controller para GET. Implementa puerto de entrada ->
 *          DIP.
 *          Usa AccountRepository (puerto de salida) incluso para leer, no
 *          acceso directo en web -> hexagonal.
 */
@Service
public class GetAccountDetailsService implements GetAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    public GetAccountDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // lectura: abre sesión para lazy de transactions. Ideal sería readOnly = true
    public AccountDetailsDto getById(String accountId) {
        // Query simple por id, no necesita Command. @PathVariable String -> VO que
        // valida UUID
        var id = new AccountId(accountId);

        // Misma excepción de dominio que Deposit/Withdraw -> GlobalExceptionHandler
        // responde 404 uniforme
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        // Aggregate -> DTO. Aquí recorre account.getTransactions() y genera
        // List<TransactionDto>
        return MapToAccountDetailsDto.from(account);
    }
}