package com.banking.transactions.domain.port;

import java.util.Optional;
import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;

/**
 * PUERTO DE SALIDA (Port Out) - Contrato del Dominio hacia el exterior
 * 
 * PATH: domain/port/ -> Va aquí porque es el DOMINIO el que define lo que
 * NECESITA,
 * no la infraestructura lo que OFRECE. Principio de Inversión de Dependencias.
 * El dominio no sabe si es JPA, Mongo o memoria. Solo dice: "necesito guardar y
 * buscar".
 * 
 * POR QUÉ public interface: Es un CONTRATO. public para que
 * infrastructure/persistence/
 * pueda implementarlo (AccountJpaAdapter). Si fuera package-private, el adapter
 * no podría verlo.
 * 
 * POR QUÉ interface y no class: El dominio solo declara QUÉ necesita, no CÓMO
 * se hace.
 * La implementación real (JPA, JDBC) vive en infrastructure. Así el Core no
 * depende de Spring.
 * 
 * POR QUÉ sin anotaciones de Spring (@Repository): Porque domain/ no debe
 * importar
 * org.springframework. Si le pones @Repository, atas el dominio a Spring Data.
 * Limpio = sin frameworks.
 * 
 * POR QUÉ Optional<Account> findById: DDD puro. Una cuenta puede no existir.
 * Optional obliga al UseCase a pensar el caso "no encontrado" y lanzar
 * AccountNotFoundException,
 * en vez de devolver null y provocar NullPointer.
 * 
 * POR QUÉ AccountId y no String: Usas Value Object, no primitivo. Type Safety.
 * Evitas confundir accountId con customerId. El dominio habla en sus tipos, no
 * en Strings.
 */
public interface AccountRepository {

    // Guarda o actualiza el Aggregate Root completo (cuenta + transacciones)
    Account save(Account account);

    // Recupera el agregado por su identidad. Optional = puede no existir
    Optional<Account> findById(AccountId accountId);
}