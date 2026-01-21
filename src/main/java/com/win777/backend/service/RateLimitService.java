package com.win777.backend.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for rate limiting.
 * Uses Bucket4j for in-memory token bucket rate limiting.
 */
@Service
public class RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    /**
     * Resolves a bucket for the given key (e.g., IP address or user ID).
     * Creates a new bucket if one doesn't exist.
     * 
     * @param key the rate limit key
     * @return the bucket for rate limiting
     */
    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    /**
     * Creates a new bucket with default rate limiting configuration.
     * Limit: 10 requests per minute
     * 
     * @return a new bucket
     */
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Tries to consume a token from the bucket.
     * 
     * @param key the rate limit key
     * @return true if token consumed successfully, false if rate limit exceeded
     */
    public boolean tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsume(1);
    }
}
