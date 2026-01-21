package dev.gabriel.idempotent_payment_gateway.controller;

import dev.gabriel.idempotent_payment_gateway.model.dtos.AccountBalanceDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionRequestDto;
import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionResponseDto;
import dev.gabriel.idempotent_payment_gateway.service.AccountService;
import dev.gabriel.idempotent_payment_gateway.service.IdempotencyService;
import dev.gabriel.idempotent_payment_gateway.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TransactionRequestDto requestDto
            ) {
        TransactionResponseDto cachedResponse = idempotencyService.get(idempotencyKey);

        if (cachedResponse != null) {
            return ResponseEntity.ok(cachedResponse);
        }

        TransactionResponseDto responseDto = transactionService.processTransaction(requestDto);
        idempotencyService.save(idempotencyKey, responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
