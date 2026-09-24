package com.banking.transactions.domain.model;

import java.util.UUID;

/**
 * Value Object - Identidad del Aggregate Account.
 * record = inmutable y equals/hashCode por valor, no por referencia. Evita
 * primitive obsession de usar String suelto.
 * El dominio genera su propia identidad (no la DB) via UUID.
 */
public record AccountId(String value) {

    public static AccountId newId() {
        return new AccountId(UUID.randomUUID().toString());
    }
}