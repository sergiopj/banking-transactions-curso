package com.banking.transactions.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.banking.transactions.infrastructure.persistence.AccountEntity;

// Repositorio Spring Data para AccountEntity
// Usamos String (UUID) como PK y no Long/Integer para no atarnos a secuencias de la BD
// @Repository marca el rol DDD, habilita la traducción de excepciones a DataAccessException y es especialización de @Component
@Repository
public interface SpringDataAccountRepository extends JpaRepository<AccountEntity, String> {

}