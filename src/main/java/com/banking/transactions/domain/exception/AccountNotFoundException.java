package com.banking.transactions.domain.exception;

import com.banking.transactions.domain.model.AccountId;

/**
 * Excepción de dominio - Cuenta no encontrada.
 * En domain/exception porque es lenguaje ubicuo, no técnica de Spring/JPA.
 * Unchecked (RuntimeException) para no ensuciar puertos con try-catch; la
 * traduce a 404 GlobalExceptionHandler.
 * Recibe AccountId VO y no String: asegura que el id ya era un UUID válido
 * antes de decir "no encontrado".
 * Mensaje para logs/debug, el cliente ve ApiError bonito desde infra/web.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(AccountId accountId) {
        super("Account not found with id: " + (accountId != null ? accountId.value() : "null"));
    }
}