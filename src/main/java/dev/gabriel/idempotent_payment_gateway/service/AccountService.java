package dev.gabriel.idempotent_payment_gateway.service;

import dev.gabriel.idempotent_payment_gateway.model.dtos.AccountBalanceDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.CreateAccountDto;
import dev.gabriel.idempotent_payment_gateway.model.entities.BankAccount;
import dev.gabriel.idempotent_payment_gateway.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final BankAccountRepository accountRepository;

    /**
     * creates a new bank account with the provided details
     *
     * @param dto the details required to create a new account.
     * @return an {@link AccountBalanceDto} representing the newly created account.
     */
    public AccountBalanceDto create(CreateAccountDto dto) {
        BankAccount account = new BankAccount();
        account.setName(dto.name());
        account.setBalance(dto.initialBalance());

        accountRepository.save(account);

        return new AccountBalanceDto(
                account.getId(),
                account.getName(),
                account.getBalance()
        );
    }

    /**
     * retrieves the current state of an account
     *
     * @param accountId the unique identifier of the account.
     * @return an {@link AccountBalanceDto} with current balance.
     * @throws IllegalArgumentException if the account is not found.
     */
    public AccountBalanceDto getAccountBalance(UUID accountId) {
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("account not found"));

        return new AccountBalanceDto(
                account.getId(),
                account.getName(),
                account.getBalance()
        );
    }
}
