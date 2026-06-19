package com.isyoudwn.market_service.infrastructure.kis.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KisRedisRepository implements KisApprovalKeyRepository {

    private static final String APPROVAL_KEY = "kis:approval-key";
    private static final Duration EXPIRATION = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public void save(String approvalKey) {
        redisTemplate .opsForValue().set(APPROVAL_KEY, approvalKey, EXPIRATION);
    }

    @Override
    public Optional<String> find() {
        String approvalKey = redisTemplate.opsForValue().get(APPROVAL_KEY);

        return Optional.ofNullable(approvalKey);
    }
}
