package dev.gabriel.idempotent_payment_gateway.controller;

import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionRequestDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionResponseDto;
import dev.gabriel.idempotent_payment_gateway.service.IdempotencyService;
import dev.gabriel.idempotent_payment_gateway.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "endpoints for processing payments")
public class TransactionController {

    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    @Operation(summary = "Process a transaction", description = "debits or credits an account. guarantees idempotency via key.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "transaction processed successfully"),
            @ApiResponse(responseCode = "200", description = "transaction returned from cache (idempotent)")
    })
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @Parameter(description = "unique key to ensure idempotency", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TransactionRequestDto requestDto
    ) {
        // check cache first
        TransactionResponseDto cachedResponse = idempotencyService.get(idempotencyKey);

        if (cachedResponse != null) {
            return ResponseEntity.ok(cachedResponse);
        }

        // process transaction
        TransactionResponseDto responseDto = transactionService.processTransaction(requestDto);

        // save to cache
        idempotencyService.save(idempotencyKey, responseDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}