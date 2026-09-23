package com.banking.transactions.domain.exception;

import com.banking.transactions.domain.model.AccountId;

/**
 * EXCEPCIÓN DE DOMINIO - Cuenta no encontrada
 * 
 * PATH: domain/exception/ -> Va aquí porque es parte del lenguaje ubicuo.
 * No es una excepción técnica de Spring ni de JPA. Es una regla de negocio:
 * "no puedo operar sobre una cuenta que no existe".
 * 
 * POR QUÉ extends RuntimeException y no Exception: En DDD las excepciones de
 * dominio son unchecked. No obligas a cada UseCase a hacer try-catch. Las
 * capturas en un solo sitio: infrastructure/web/GlobalExceptionHandler que
 * las traduce a 404. Si fuera checked, ensuciarías todos los puertos.
 * 
 * POR QUÉ recibe AccountId y no String: Trabajas con Value Objects, no
 * primitivos.
 * AccountId ya valida que es un UUID válido. Si recibieras String, podrías
 * lanzar "Account not found" incluso con un id mal formado. Así aseguras que
 * el id que no se encontró al menos era válido.
 * 
 * POR QUÉ super("Account not found with id: " + accountId): El mensaje es
 * para logs, no para el cliente. El cliente verá un ApiError bonito desde
 * el GlobalExceptionHandler. Aquí dejas trazabilidad para debug.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(AccountId accountId) {
        super("Account not found with id: " + accountId);
    }
}