package dev.gabriel.idempotent_payment_gateway.model.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponseDto(
        UUID transactionId,
        String status,
        BigDecimal newBalance,
        String message
) {}
