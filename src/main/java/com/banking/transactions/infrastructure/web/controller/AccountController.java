package com.banking.transactions.infrastructure.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.transactions.application.dto.CreateAccountCommand;
import com.banking.transactions.application.dto.DepositMoneyCommand;
import com.banking.transactions.application.dto.WithdrawMoneyCommand;
import com.banking.transactions.application.port.CreateAccountUseCase;
import com.banking.transactions.application.port.DepositMoneyUseCase;
import com.banking.transactions.application.port.GetAccountDetailsUseCase;
import com.banking.transactions.application.port.WithdrawMoneyUseCase;
import com.banking.transactions.infrastructure.web.dto.AccountResponse;
import com.banking.transactions.infrastructure.web.dto.CreateAccountRequest;
import com.banking.transactions.infrastructure.web.dto.DepositRequest;
import com.banking.transactions.infrastructure.web.dto.WithdrawRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

// Adapter de entrada - Expone los casos de uso por REST
@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
public class AccountController {

    // contratos - puertos de entrada
    private final CreateAccountUseCase createAccountUseCase;
    private final DepositMoneyUseCase depositMoneyUseCase;
    private final WithdrawMoneyUseCase withdrawMoneyUseCase;
    private final GetAccountDetailsUseCase getAccountDetailsUseCase;

    // POST /api/accounts - crea cuenta
    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request) {
        var command = new CreateAccountCommand(request.customerId(), request.initialBalance().doubleValue());
        var dto = createAccountUseCase.createAccount(command);
        return ResponseEntity.status(201).body(AccountResponse.fromDto(dto));
    }

    // POST /{id}/deposit - ingresa dinero
    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable String id,
            @Valid @RequestBody DepositRequest request) {
        var command = new DepositMoneyCommand(id, request.amount().doubleValue());
        var dto = depositMoneyUseCase.depositMoney(command);
        return ResponseEntity.ok(AccountResponse.fromDto(dto));
    }

    // POST /{id}/withdraw - retira dinero
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable String id,
            @Valid @RequestBody WithdrawRequest request) {
        var command = new WithdrawMoneyCommand(id, request.amount().doubleValue());
        var dto = withdrawMoneyUseCase.withdrawMoney(command);
        return ResponseEntity.ok(AccountResponse.fromDto(dto));
    }

    // GET /{id} - obtiene cuenta por id
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable String id) {
        var dto = getAccountDetailsUseCase.getById(id);
        return ResponseEntity.ok(AccountResponse.fromDto(dto));
    }

    // TODO listar cuentas paginado, solo superadmin
}