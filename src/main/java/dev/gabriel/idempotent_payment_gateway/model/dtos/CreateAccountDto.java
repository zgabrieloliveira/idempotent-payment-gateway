package dev.gabriel.idempotent_payment_gateway.model.dtos;

import java.math.BigDecimal;

public record CreateAccountDto(
        String name,
        BigDecimal initialBalance
) {}