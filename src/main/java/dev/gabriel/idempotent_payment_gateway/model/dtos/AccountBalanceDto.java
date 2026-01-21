package dev.gabriel.idempotent_payment_gateway.model.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountBalanceDto(
        UUID accountId,
        String name,
        BigDecimal balance
) {}