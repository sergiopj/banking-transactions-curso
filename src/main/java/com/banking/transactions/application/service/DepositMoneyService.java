package com.banking.transactions.application.service;

import org.springframework.stereotype.Service;

import com.banking.transactions.application.dto.AccountDetailsDto;
import com.banking.transactions.application.dto.DepositMoneyComand;
import com.banking.transactions.application.dto.MapToAccountDetailsDto;
import com.banking.transactions.application.port.DepositMoneyUseCase;
import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.port.AccountRepository;

import jakarta.transaction.Transactional;

@Service
public class DepositMoneyService implements DepositMoneyUseCase {

    private final AccountRepository accountRepository;

    public DepositMoneyService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public AccountDetailsDto depositMoney(DepositMoneyComand command) {
        var accountId = new AccountId(command.accountId());
        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        account.deposit(Money.of(String.valueOf(command.amount())));
        accountRepository.save(account);

        return MapToAccountDetailsDto.from(account);
    }

}
