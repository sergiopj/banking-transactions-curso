package com.banking.transactions.domain.exception;

/**
 * Excepción de dominio - Valor inválido en VO Money.
 * En domain/exception porque es regla del Core (Money <= 0). La lanza
 * Money/Account.
 * Unchecked para no ensuciar constructores con throws; el 400 lo decide
 * infra/web.
 * Solo mensaje con la invariante rota.
 */
public class NegativeMoneyException extends RuntimeException {

    public NegativeMoneyException(String message) {
        super(message);
    }
}