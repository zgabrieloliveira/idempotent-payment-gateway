package dev.gabriel.idempotent_payment_gateway.service;

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

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final BankAccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;
    private final BankEntryRepository entryRepository;

    @Transactional // either completes fully or rolls back
    public TransactionResponseDto processTransaction(TransactionRequestDto request) {
        // search for account, locking it for update
        BankAccount account = accountRepository
                .findByIdWithLock(request.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        EntryType type = EntryType.valueOf(request.type().toUpperCase());

        // determine transaction type
        switch (type) {
            case DEBIT -> {
                // check for sufficient funds
                if (account.getBalance().compareTo(request.amount()) < 0) {
                    return new TransactionResponseDto(
                            null,
                            "FAILED",
                            account.getBalance(),
                            "Insufficient funds"
                    );
                }
                // perform debit
                account.setBalance(account.getBalance().subtract(request.amount()));
            }
            case CREDIT -> {
                // perform credit
                account.setBalance(account.getBalance().add(request.amount()));
            }
            default -> throw new IllegalArgumentException("Invalid transaction type");
        }

        // create transaction record
        BankTransaction transaction = BankTransaction.builder()
                .description("Transaction for account " + account.getId())
                .amount(request.amount())
                .build();

        accountRepository.save(account); // persist updated balance

        BankEntry entry = BankEntry.builder()
                .account(account)
                .transaction(transaction)
                .amount(request.type().equalsIgnoreCase("DEBIT") ? request.amount().negate() : request.amount())
                .type(type)
                .build();

        entryRepository.save(entry);

        return new TransactionResponseDto(
                account.getId(),
                "COMPLETED",
                account.getBalance(),
                null
        );
    }

}
