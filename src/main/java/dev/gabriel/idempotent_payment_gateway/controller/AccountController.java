package dev.gabriel.idempotent_payment_gateway.controller;

import dev.gabriel.idempotent_payment_gateway.model.dtos.AccountBalanceDto;
import dev.gabriel.idempotent_payment_gateway.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountBalanceDto> getBalance(@PathVariable UUID id) {
        AccountBalanceDto balanceDto = accountService.getAccountBalance(id);
        return ResponseEntity.ok(balanceDto);
    }

}
