package com.banking.transactions.infrastructure.mapper;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.model.Money;
import com.banking.transactions.domain.model.TransactionType;
import com.banking.transactions.infrastructure.mapper.AccountMapper.AccountMapper;
import com.banking.transactions.infrastructure.persistence.AccountEntity;
import com.banking.transactions.infrastructure.persistence.TransactionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountMapper Unit Tests")
class AccountMapperTest {

    @Test
    @DisplayName("Should map JPA AccountEntity with transactions to Domain Account")
    void shouldMapEntityToDomain() {
        AccountEntity entity = new AccountEntity();
        entity.setId("acc-123");
        entity.setCustomerId("cust-456");
        entity.setBalance(new BigDecimal("250.00"));

        TransactionEntity te = new TransactionEntity();
        te.setId("tx-789");
        te.setType(TransactionType.DEPOSIT);
        te.setAmount(new BigDecimal("50.00"));
        te.setCreatedAt(Instant.now());
        entity.addTransaction(te);

        Account domain = AccountMapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals("acc-123", domain.getId().value());
        assertEquals("cust-456", domain.getCustomerId());
        assertEquals(Money.of("250.00"), domain.getBalance());
        assertEquals(1, domain.getTransactions().size());
        assertEquals("tx-789", domain.getTransactions().getFirst().getId());
    }

    @Test
    @DisplayName("Should map Domain Account to JPA AccountEntity")
    void shouldMapDomainToEntity() {
        AccountId id = new AccountId("acc-123");
        Account domain = new Account(id, "cust-456", Money.of("100.00"));
        domain.deposit(Money.of("25.00"));

        AccountEntity entity = AccountMapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals("acc-123", entity.getId());
        assertEquals("cust-456", entity.getCustomerId());
        assertEquals(new BigDecimal("125.00"), entity.getBalance());
        assertEquals(1, entity.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, entity.getTransactions().getFirst().getType());
        assertNotNull(entity.getTransactions().getFirst().getCreatedAt());
        assertEquals(entity, entity.getTransactions().getFirst().getAccount());
    }
}
