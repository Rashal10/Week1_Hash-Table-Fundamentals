import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

class TokenBucket {
    private final long maxTokens;
    private final long refillIntervalMs;
    private final long refillAmount;
    private AtomicLong tokens;
    private volatile long lastRefillTime;

    public TokenBucket(long maxTokens, long refillIntervalMs, long refillAmount) {
        this.maxTokens = maxTokens;
        this.refillIntervalMs = refillIntervalMs;
        this.refillAmount = refillAmount;
        this.tokens = new AtomicLong(maxTokens);
        this.lastRefillTime = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume() {
        refill();
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        long intervals = (now - lastRefillTime) / refillIntervalMs;
        if (intervals > 0) {
            long newTokens = Math.min(maxTokens, tokens.get() + intervals * refillAmount);
            tokens.set(newTokens);
            lastRefillTime += intervals * refillIntervalMs;
        }
    }

    public long getRemainingTokens() {
        refill();
        return tokens.get();
    }

    public long getNextRefillTime() {
        return lastRefillTime + refillIntervalMs;
    }
}

public class RateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private final long maxRequests = 1000;
    private final long refillIntervalMs = 3600_000; // 1 hour
    private final long refillAmount = 1000;

    public boolean checkRateLimit(String clientId) {
        TokenBucket bucket = clients.computeIfAbsent(clientId, id -> new TokenBucket(maxRequests, refillIntervalMs, refillAmount));
        boolean allowed = bucket.tryConsume();
        if (allowed) {
            System.out.println("Allowed (" + bucket.getRemainingTokens() + " requests remaining)");
        } else {
            long retryAfter = (bucket.getNextRefillTime() - System.currentTimeMillis()) / 1000;
            System.out.println("Denied (0 requests remaining, retry after " + retryAfter + "s)");
        }
        return allowed;
    }

    public void getRateLimitStatus(String clientId) {
        TokenBucket bucket = clients.get(clientId);
        if (bucket == null) {
            System.out.println("Client not found.");
            return;
        }
        long used = maxRequests - bucket.getRemainingTokens();
        long reset = bucket.getNextRefillTime() / 1000;
        System.out.println("{used: " + used + ", limit: " + maxRequests + ", reset: " + reset + "}");
    }

    public static void main(String[] args) throws InterruptedException {
        RateLimiter limiter = new RateLimiter();
        String clientId = "abc123";

        for (int i = 0; i < 5; i++) {
            limiter.checkRateLimit(clientId);
        }

        limiter.getRateLimitStatus(clientId);
    }
}