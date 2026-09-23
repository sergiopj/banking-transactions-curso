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
 * ADAPTER DE CASO DE USO - Implementación de Puerto de Entrada
 * 
 * PATH: application/service/ -> Va aquí y no en infrastructure/ porque es
 * lógica de aplicación, no detalle técnico. Es el que orquesta dominio.
 * En tu estructura ideal sería application/usecase/ pero service/ te vale.
 * 
 * POR QUÉ @Service: Marca la clase como bean de Spring. @SpringBootApplication
 * en infrastructure/config/ la detecta con @ComponentScan y la inyecta en
 * AccountController.
 * 
 * POR QUÉ implements CreateAccountUseCase: Inversión de Dependencia. El web
 * solo conoce la interface del port/. Tú aquí pones el CÓMO.
 * 
 * POR QUÉ private final AccountRepository: Puerto de Salida. No conoce JPA,
 * solo la interface de domain/port/. El impl real está en
 * infrastructure/persistence/.
 */
@Service
public class CreateAccountService implements CreateAccountUseCase {

    private final AccountRepository accountRepository;

    // POR QUÉ constructor y no @Autowired: Constructor injection = inmutable +
    // testeable
    public CreateAccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional // POR QUÉ: Si save() falla, rollback. La cuenta no queda a medias
    public AccountDetailsDto createAccount(CreateAccountCommand command) {
        // POR QUÉ este ternario: command.initialBalance() es Double objeto (nullable)
        // Si viene null desde JSON, no es 0.0, es "no mandó saldo". Lo convertimos a
        // Money.zero()
        // Si viene con valor, lo pasamos a String para usar Money.of(String) que es
        // exacto con BigDecimal
        Money initialBalance = (command.initialBalance() == null)
                ? Money.zero()
                : Money.of(String.valueOf(command.initialBalance()));

        // POR QUÉ AccountId.newId(): El Agregado Account es quien genera su identidad,
        // no la DB
        // POR QUÉ new Account(...): Creas el Agregado en estado válido. Su constructor
        // ya valida
        Account account = new Account(
                AccountId.newId(),
                command.customerId(),
                initialBalance);

        accountRepository.save(account);

        // POR QUÉ MapToAccountDetailsDto.from(account): Nunca devuelves el Agregado al
        // exterior
        // Mapeas a DTO de lectura (Read Model) con primitivos listos para JSON
        return MapToAccountDetailsDto.from(account);
    }
}