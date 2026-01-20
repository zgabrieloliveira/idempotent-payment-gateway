package dev.gabriel.idempotent_payment_gateway.repository;

import dev.gabriel.idempotent_payment_gateway.model.entities.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * repository responsible for persistence operations of the {@link BankTransaction} entity.
 * this entity represents the "header" or "receipt" of a financial operation.
 */
@Repository
public interface BankTransactionRepository extends JpaRepository<BankTransaction, UUID> {
}
