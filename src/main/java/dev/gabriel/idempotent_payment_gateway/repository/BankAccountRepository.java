package dev.gabriel.idempotent_payment_gateway.repository;

import dev.gabriel.idempotent_payment_gateway.model.entities.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * repository responsible for persistence operations of the {@link BankAccount} entity.
 */
@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {

    /**
     * retrieves an account by id and applies a pessimistic lock (pessimistic_write) on the database record.
     * <p>
     * this method executes a {@code select ... for update}, preventing other transactions from
     * reading or modifying this record until the current transaction finishes (commit or rollback).
     * <p>
     * must be used in critical financial operations (debits/credits) to avoid
     * "race conditions" where two threads try to modify the balance simultaneously.
     *
     * @param id the unique identifier (uuid) of the account.
     * @return an {@link Optional} containing the account, if found.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM BankAccount a WHERE a.id = :id")
    Optional<BankAccount> findByIdWithLock(UUID id);
}