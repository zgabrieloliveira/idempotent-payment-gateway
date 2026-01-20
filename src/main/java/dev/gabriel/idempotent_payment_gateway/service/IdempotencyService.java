package dev.gabriel.idempotent_payment_gateway.service;

import dev.gabriel.idempotent_payment_gateway.model.dtos.TransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long TTL_HOURS = 24; // cache expires in 24h

    public TransactionResponseDto get(String idempotencyKey) {
        return (TransactionResponseDto) redisTemplate.opsForValue().get(idempotencyKey);
    }

    public void save(String idempotencyKey, TransactionResponseDto responseDto) {
        redisTemplate.opsForValue().set(
                idempotencyKey,
                responseDto,
                TTL_HOURS,
                TimeUnit.HOURS
        );
    }
}
