package dev.gabriel.idempotent_payment_gateway.service;

import dev.gabriel.idempotent_payment_gateway.model.dtos.AccountBalanceDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionRequestDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionResponseDto;
import dev.gabriel.idempotent_payment_gateway.model.entities.BankAccount;
import dev.gabriel.idempotent_payment_gateway.model.entities.BankEntry;
import dev.gabriel.idempotent_payment_gateway.model.entities.BankTransaction;
import dev.gabriel.idempotent_payment_gateway.model.enums.EntryType;
import dev.gabriel.idempotent_payment_gateway.repository.BankAccountRepository;
import dev.gabriel.idempotent_payment_gateway.repository.BankEntryRepository;
import dev.gabriel.idempotent_payment_gateway.repository.BankTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * service responsible for processing financial transactions.
 * manages account balances, transaction history, and ensures data consistency using pessimistic locks.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final BankEntryRepository entryRepository;

    /**
     * processes a transaction (debit or credit) for a specific account.
     * <p>
     * this method performs the following steps atomically:
     * 1. locks the account record in the database (pessimistic write) to prevent race conditions.
     * 2. validates the transaction type and checks for sufficient funds (if debit).
     * 3. updates the account balance.
     * 4. creates and persists a transaction header record.
     * 5. creates and persists a ledger entry record linked to the transaction and account.
     *
     * @param request the transaction request containing account id, amount, and type.
     * @return a {@link TransactionResponseDto} with the transaction status and new balance.
     * @throws IllegalArgumentException if the account is not found or transaction type is invalid.
     */
    @Transactional // either completes fully or rolls back
    public TransactionResponseDto processTransaction(TransactionRequestDto request) {
        // search for account, locking it for update
        BankAccount account = accountRepository
                .findByIdWithLock(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("account not found"));

        EntryType type = EntryType.valueOf(request.type().toUpperCase());

        // determine transaction type and update balance
        switch (type) {
            case DEBIT -> {
                // check for sufficient funds
                if (account.getBalance().compareTo(request.amount()) < 0) {
                    return new TransactionResponseDto(
                            null,
                            "FAILED",
                            account.getBalance(),
                            "insufficient funds"
                    );
                }
                // perform debit
                account.setBalance(account.getBalance().subtract(request.amount()));
            }
            case CREDIT -> {
                // perform credit
                account.setBalance(account.getBalance().add(request.amount()));
            }
            default -> throw new IllegalArgumentException("invalid transaction type");
        }

        // create transaction record
        BankTransaction transaction = BankTransaction.builder()
                .description("transaction for account " + account.getId())
                .amount(request.amount())
                .build();

        // save the transaction header first to generate the id
        transactionRepository.save(transaction);

        // persist updated balance
        accountRepository.save(account);

        // create the ledger entry linked to the saved transaction
        BankEntry entry = BankEntry.builder()
                .account(account)
                .transaction(transaction)
                .amount(request.type().equalsIgnoreCase("DEBIT") ? request.amount().negate() : request.amount())
                .type(type)
                .build();

        // save the entry
        entryRepository.save(entry);

        return new TransactionResponseDto(
                transaction.getId(),
                "COMPLETED",
                account.getBalance(),
                null
        );
    }

}