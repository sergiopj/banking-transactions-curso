package com.banking.transactions.domain.exception;

/**
 * Excepción de dominio - Regla de negocio "sin fondos".
 * En domain/exception porque es lenguaje ubicuo, no error técnico de app/infra.
 * Unchecked (RuntimeException) para no contaminar firmas con try/catch; fluye
 * hasta GlobalExceptionHandler.
 * Solo mensaje: explica la invariante rota, el código HTTP/ApiError lo pone el
 * adapter web.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}