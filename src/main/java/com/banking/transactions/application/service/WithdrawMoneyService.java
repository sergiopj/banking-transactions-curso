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
 * ADAPTER DE CASO DE USO - Retirar Dinero
 * 
 * PATH: application/service/ -> Capa de aplicación, orquesta.
 * No sabe de JSON ni de tablas. Solo: DTO in -> Agregado -> save -> DTO out.
 * 
 * POR QUÉ @Service: Bean de caso de uso que inyecta AccountController.
 * 
 * POR QUÉ implements WithdrawMoneyUseCase: Implementas el Puerto de Entrada.
 * El adapter web depende de la interface, no de ti.
 * 
 * POR QUÉ final AccountRepository: Puerto de Salida hacia persistence/.
 * El dominio no sabe si es JPA, Mongo o InMemory.
 */
@Service
public class WithdrawMoneyService implements WithdrawMoneyUseCase {

    private final AccountRepository accountRepository;

    public WithdrawMoneyService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // find + withdraw + save atómico. Si falla la regla de negocio, rollback
    public AccountDetailsDto withdrawMoney(WithdrawMoneyCommand command) {
        // 1. String -> Value Object. AccountId valida formato UUID
        var accountId = new AccountId(command.accountId());

        // 2. Carga Agregado o falla con excepción de dominio -> luego
        // GlobalExceptionHandler lo pasa a 404
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // 3. Delega al dominio. account.withdraw() valida saldo y lanza
        // InsufficientBalanceException si no hay
        // POR QUÉ String.valueOf: Money.of(String) es exacto con BigDecimal, tu Command
        // trae double
        account.withdraw(Money.of(String.valueOf(command.amount())));

        // 4. Persiste el nuevo estado + la Transaction creada dentro de withdraw()
        accountRepository.save(account);

        // 5. Agregado -> DTO de lectura. Nunca devuelves Account con Money y
        // List<Transaction> al exterior
        return MapToAccountDetailsDto.from(account);
    }
}