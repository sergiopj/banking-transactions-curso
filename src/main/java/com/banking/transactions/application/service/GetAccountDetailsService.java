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
 * ADAPTER DE CASO DE USO - Consulta (Query)
 * 
 * PATH: application/service/ -> Este es el lado Q de CQRS. No modifica estado,
 * solo lee. Los otros 3 (Create, Deposit, Withdraw) son el lado C.
 * 
 * POR QUÉ @Service: Mismo bean que los de escritura, pero sin lógica de
 * negocio. Solo carga y mapea. Spring lo inyecta en AccountController para GET.
 * 
 * POR QUÉ implements GetAccountDetailsUseCase: Puerto de Entrada de lectura.
 * El web depende de la interface, no de esta clase.
 * 
 * POR QUÉ AccountRepository aquí también: Incluso para leer usas el Puerto de
 * Salida. No haces un JdbcTemplate directo en el controller. Mantienes
 * hexagonal.
 */
@Service
public class GetAccountDetailsService implements GetAccountDetailsUseCase {

    private final AccountRepository accountRepository;

    public GetAccountDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // POR QUÉ: Aunque sea lectura, abre sesión para lazy de transactions si usas
                   // JPA
    // Ideal sería @Transactional(readOnly = true) para optimizar, pero jakarta te
    // vale
    public AccountDetailsDto getById(String accountId) {
        // POR QUÉ String accountId directo y no un Command: Es una query por id, no
        // necesitas
        // un objeto con varios campos. El controller pasa el @PathVariable tal cual.
        // Aquí lo conviertes a Value Object para validar formato UUID
        var id = new AccountId(accountId);

        // POR QUÉ orElseThrow: Misma excepción de dominio que en Deposit/Withdraw
        // Así tu GlobalExceptionHandler siempre responde 404 igual
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        // POR QUÉ MapToAccountDetailsDto.from(account): Agregado -> DTO con primitivos
        // Aquí es donde entra TransactionDto que viste antes. El mapper recorre
        // account.getTransactions() y crea List<TransactionDto>
        return MapToAccountDetailsDto.from(account);
    }
}