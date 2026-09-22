package com.banking.transactions.domain.exception;

/**
 * EXCEPCIÓN DE DOMINIO - Valor inválido en Value Object Money
 * 
 * PATH: domain/exception/ -> Excepción del Core. La lanza Money y Account
 * cuando
 * intentas crear dinero con valor <= 0. Es lenguaje del banco, no error
 * técnico.
 * 
 * POR QUÉ public class: public porque la usan Money.of(), Account.deposit() y
 * Account.withdraw(). La tiene que ver application/ y el ExceptionHandler.
 * 
 * POR QUÉ extends RuntimeException: Unchecked en DDD. Si fuera checked
 * (Exception),
 * obligarías a poner throws en todos los constructores de Money y ensucias el
 * dominio.
 * Como Runtime, el dominio protege sus invariantes y deja que infrastructure
 * decida
 * si devuelve 400 Bad Request.
 * 
 * POR QUÉ solo String message: No necesita más datos. El mensaje ya dice qué
 * regla
 * de Money se rompió. El código HTTP (400) lo pone el adapter web, no el
 * dominio.
 */
public class NegativeMoneyException extends RuntimeException {

    public NegativeMoneyException(String message) {
        super(message);
    }
}