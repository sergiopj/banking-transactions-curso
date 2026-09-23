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
 * ADAPTER DE CASO DE USO - Ingresar Dinero
 * 
 * PATH: application/service/ -> Orquesta dominio. No sabe de HTTP ni de JPA.
 * Solo conoce Ports In (que implementa) y Ports Out (AccountRepository).
 * 
 * POR QUÉ @Service: Bean de aplicación que Spring inyecta en AccountController.
 * 
 * POR QUÉ implements DepositMoneyUseCase: El controller depende de la
 * interface,
 * tú aquí metes la lógica concreta. Hexagonal + DIP.
 */
@Service
public class DepositMoneyService implements DepositMoneyUseCase {

    private final AccountRepository accountRepository; // Puerto de Salida

    public DepositMoneyService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // POR QUÉ: find + deposit + save deben ser atómicos. Si falla save, no queda el
                   // deposito en memoria
    public AccountDetailsDto depositMoney(DepositMoneyCommand command) {
        // POR QUÉ new AccountId(command.accountId()): Conviertes String del DTO a Value
        // Object de dominio
        // El VO valida formato UUID dentro
        var accountId = new AccountId(command.accountId());

        // POR QUÉ findById + orElseThrow: El caso de uso es quien decide qué pasa si no
        // existe
        // Lanza excepción de dominio AccountNotFoundException, no una de Spring
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // POR QUÉ Money.of(String.valueOf(command.amount())): Tu Command trae double
        // Tu Value Object Money solo acepta String para usar BigDecimal(String) exacto
        // Aquí haces la conversión en el borde de aplicación
        // account.deposit() ya crea la Transaction interna y actualiza el balance
        account.deposit(Money.of(String.valueOf(command.amount())));

        accountRepository.save(account);

        // POR QUÉ MapToAccountDetailsDto.from(account): Devuelves DTO de lectura, no el
        // Agregado
        return MapToAccountDetailsDto.from(account);
    }
}