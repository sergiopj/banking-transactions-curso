package com.banking.transactions.infrastructure.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.banking.transactions.domain.model.Account;
import com.banking.transactions.domain.model.AccountId;
import com.banking.transactions.domain.port.AccountRepository;
import com.banking.transactions.infrastructure.mapper.AccountMapper.AccountMapper;
import com.banking.transactions.infrastructure.persistence.AccountEntity;
import com.banking.transactions.infrastructure.repository.SpringDataAccountRepository;

import lombok.AllArgsConstructor;

// Adapter de salida (Infrastructure). Implementa el puerto AccountRepository del dominio.
// Es el puente entre el dominio puro y Spring Data JPA. El dominio nunca ve JPA.
@Component
@AllArgsConstructor // genera constructor con el SpringDataAccountRepository para inyección por
                    // constructor
public class AccountRepositoryAdapter implements AccountRepository {

    private final SpringDataAccountRepository springDataAccountRepository;

    // Guarda un agregado de dominio en BD
    // 1. Convierte Dominio -> Entity con toEntity (el dominio no tiene @Entity, la
    // entity sí)
    // 2. Guarda con JpaRepository.save()
    // 3. Convierte Entity guardada -> Dominio con toDomain para devolver el
    // agregado reconstituido con IDs/timestamps de BD
    @Override
    public Account save(Account account) {
        AccountEntity accountEntity = AccountMapper.toEntity(account);
        AccountEntity saved = springDataAccountRepository.save(accountEntity);
        return AccountMapper.toDomain(saved);
    }

    // Busca por ID de dominio
    // Recibe un Value Object AccountId y extrae el String interno con .value()
    // porque Spring Data trabaja con String
    // findById devuelve Optional<Entity> y lo mapeamos a Optional<Domain> con
    // toDomain
    // Así la capa de aplicación solo trabaja con objetos de dominio, nunca con
    // entidades JPA
    @Override
    public Optional<Account> findById(AccountId accountId) {
        return springDataAccountRepository.findById(accountId.value())
                .map(AccountMapper::toDomain); // referencia a método estático que convierte Entity -> Dominio
    }
}