package com.zm.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private static final long IDEMPOTENCY_TTL_HOURS = 24;

    // Fallback in-memory storage for testing
    private final ConcurrentHashMap<String, Long> inMemoryStorage = new ConcurrentHashMap<>();
    private final boolean useRedis;

    @Autowired
    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.useRedis = redisTemplate != null;
    }

    public boolean isDuplicateRequest(String idempotencyKey) {
        if (useRedis) {
            return isDuplicateRequestRedis(idempotencyKey);
        } else {
            return isDuplicateRequestInMemory(idempotencyKey);
        }
    }

    private boolean isDuplicateRequestRedis(String idempotencyKey) {
        String redisKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
        Boolean exists = redisTemplate.hasKey(redisKey);

        if (Boolean.TRUE.equals(exists)) {
            log.warn("Duplicate request detected with idempotency key: {}", idempotencyKey);
            return true;
        }

        // Mark this key as used
        redisTemplate.opsForValue().set(redisKey, "1", IDEMPOTENCY_TTL_HOURS, TimeUnit.HOURS);
        return false;
    }

    private boolean isDuplicateRequestInMemory(String idempotencyKey) {
        String fullKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
        Long timestamp = inMemoryStorage.get(fullKey);

        if (timestamp != null && (System.currentTimeMillis() - timestamp) < TimeUnit.HOURS.toMillis(IDEMPOTENCY_TTL_HOURS)) {
            log.warn("Duplicate request detected with idempotency key: {}", idempotencyKey);
            return true;
        }

        // Mark this key as used
        inMemoryStorage.put(fullKey, System.currentTimeMillis());
        return false;
    }

    public void markKeyAsUsed(String idempotencyKey) {
        if (useRedis) {
            String redisKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            redisTemplate.opsForValue().set(redisKey, "1", IDEMPOTENCY_TTL_HOURS, TimeUnit.HOURS);
        } else {
            String fullKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            inMemoryStorage.put(fullKey, System.currentTimeMillis());
        }
    }

    public void removeKey(String idempotencyKey) {
        if (useRedis) {
            String redisKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            redisTemplate.delete(redisKey);
        } else {
            String fullKey = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
            inMemoryStorage.remove(fullKey);
        }
    }
}