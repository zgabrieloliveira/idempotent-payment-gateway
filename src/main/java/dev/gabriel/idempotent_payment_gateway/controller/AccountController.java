package dev.gabriel.idempotent_payment_gateway.controller;

import dev.gabriel.idempotent_payment_gateway.model.dtos.AccountBalanceDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.CreateAccountDto;
import dev.gabriel.idempotent_payment_gateway.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "endpoints for account management")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new account", description = "initializes a new bank account with a starting balance")
    @ApiResponse(responseCode = "201", description = "account created successfully")
    public ResponseEntity<AccountBalanceDto> create(@RequestBody CreateAccountDto dto) {
        AccountBalanceDto createdAccount = accountService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @GetMapping("/{id}/balance")
    @Operation(summary = "Get account balance", description = "retrieves the current balance for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "balance retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "account not found")
    })
    public ResponseEntity<AccountBalanceDto> getBalance(@PathVariable UUID id) {
        AccountBalanceDto balanceDto = accountService.getAccountBalance(id);
        return ResponseEntity.ok(balanceDto);
    }

}