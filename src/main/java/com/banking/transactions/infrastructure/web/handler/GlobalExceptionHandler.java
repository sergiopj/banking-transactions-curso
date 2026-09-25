package com.banking.transactions.infrastructure.web.handler;

import java.time.Instant;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.banking.transactions.domain.exception.AccountNotFoundException;
import com.banking.transactions.domain.exception.InsufficientBalanceException;
import com.banking.transactions.domain.exception.NegativeMoneyException;
import com.banking.transactions.infrastructure.web.dto.ErrorMessage;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Intercepta excepciones de los controladores REST y devuelve un JSON
 * formateado
 * con el DTO ErrorMessage estandarizado para toda la API.
 * 
 * @RestControllerAdvice: Combina @ControllerAdvice y @ResponseBody.
 *                        Hace que cualquier excepción capturada aquí se
 *                        serialice automáticamente a JSON.
 */

/*
 * 
 * No lo llama ninguna clase de tu proyecto. Lo llama el try-catch principal de
 * Spring (DispatcherServlet) usando Java Reflection (method.invoke(...))
 * gracias a que le pusiste la etiqueta @ExceptionHandler.
 * 
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        // 404 - Cuenta no encontrada
        @ExceptionHandler(AccountNotFoundException.class)
        public ResponseEntity<ErrorMessage> handleAccountNotFound(AccountNotFoundException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.NOT_FOUND;
                ErrorMessage errorMessage = new ErrorMessage(
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI(),
                                Instant.now(),
                                List.of());
                return ResponseEntity.status(status).body(errorMessage);
        }

        // 422 o 400 - Saldo insuficiente para realizar el retiro
        @ExceptionHandler(InsufficientBalanceException.class)
        public ResponseEntity<ErrorMessage> handleInsufficientBalance(InsufficientBalanceException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                ErrorMessage errorMessage = new ErrorMessage(
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI(),
                                Instant.now(),
                                List.of());
                return ResponseEntity.status(status).body(errorMessage);
        }

        // 400 - Importe negativo o nulo
        @ExceptionHandler(NegativeMoneyException.class)
        public ResponseEntity<ErrorMessage> handleNegativeMoney(NegativeMoneyException ex,
                        HttpServletRequest request) {
                HttpStatus status = HttpStatus.BAD_REQUEST;
                ErrorMessage errorMessage = new ErrorMessage(
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI(),
                                Instant.now(),
                                List.of());
                return ResponseEntity.status(status).body(errorMessage);
        }

        // errores de validacion de entrada
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorMessage> handleValidationErrors(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                HttpStatus status = HttpStatus.BAD_REQUEST;
                String message = "Validation failed for request";
                List<String> details = ex.getBindingResult().getFieldErrors().stream().map(this::formatFieldError)
                                .toList();
                ErrorMessage body = new ErrorMessage(
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                request.getRequestURI(),
                                Instant.now(),
                                details

                );
                return ResponseEntity.status(status).body(body);

        }

        // metodo adiccional para formatear el mensaje
        private String formatFieldError(FieldError error) {
                // dos parametros con %s y %s
                return "%s: %s".formatted(
                                error.getField(),
                                error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value");
        }

}
