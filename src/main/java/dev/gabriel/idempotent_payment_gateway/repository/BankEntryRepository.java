package dev.gabriel.idempotent_payment_gateway.repository;

import dev.gabriel.idempotent_payment_gateway.model.entities.BankEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * repository responsible for persistence operations of the {@link BankEntry} entity.
 * this entity represents the individual ledger lines (debit/credit) that compose a transaction.
 */
@Repository
public interface BankEntryRepository extends JpaRepository<BankEntry, UUID> {
}
