package com.banking.transactions.domain.exception;

/**
 * EXCEPCIÓN DE DOMINIO - Regla de negocio violada
 * 
 * PATH: domain/exception/ -> Va aquí porque es parte del lenguaje ubicuo del
 * banco.
 * No va en application/ ni en infrastructure/. Es el dominio diciendo
 * "sin fondos" en su propio idioma, no un error técnico.
 * 
 * POR QUÉ public class: public porque la lanzan Account.withdraw() y la
 * capturan
 * los UseCases y el GlobalExceptionHandler de infrastructure. Tiene que ser
 * visible.
 * 
 * POR QUÉ extends RuntimeException y no Exception: En DDD y Hexagonal las
 * excepciones
 * de dominio son NO chequeadas (unchecked). Si fuera Exception (checked)
 * obligarías a
 * todo el código a hacer try/catch y contaminarías el dominio con manejo
 * técnico.
 * RuntimeException permite que fluya hasta el handler sin ensuciar firmas.
 * 
 * POR QUÉ solo constructor con message: Es una excepción de negocio pura, sin
 * campos
 * extra. El mensaje ya explica la invariante rota. No necesita código de error
 * aquí,
 * eso lo pone el adapter en infrastructure/web/exception/.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}