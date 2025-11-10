package kz.bmstu.kritinina.circuit_breaker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

@Slf4j
@Getter
public class CircuitBreaker {
    private final String name;
    private final int maxFailure;
    private final int minSuccess;
    private final Duration timeout;

    private final ReentrantLock lock = new ReentrantLock(true);
    private final Duration lockTimeout = Duration.ofMillis(100);

    private int failureCount = 0;
    private int successCount = 0;
    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private Instant lastFailureTime = null;

    public CircuitBreaker(String name, int maxFailure, int minSuccess, Duration timeout) {
        this.name = name;
        this.maxFailure = maxFailure;
        this.minSuccess = minSuccess;
        this.timeout = timeout;
    }

    public <T> T execute(Supplier<T> supplier) throws CircuitBreakerException {
        boolean acquired = false;
        try {
            acquired = lock.tryLock(lockTimeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new CircuitBreakerException("Circuit breaker lock timeout");
            }
            if (state == CircuitBreakerState.OPEN) {
                Duration timeSinceFailure = Duration.between(lastFailureTime, Instant.now());
                if (lastFailureTime != null && timeSinceFailure.compareTo(timeout) > 0) {
                    transitionTo(CircuitBreakerState.HALF_OPENED);
                } else {
                    throw new CircuitBreakerException("Circuit breaker is OPEN");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CircuitBreakerException("Interrupted while waiting for lock");
        } catch (Exception e) {
            throw new CircuitBreakerException("Unexpected error: " + e.getMessage());
        }
        finally {
            if (acquired) {
                lock.unlock();
            }
        }
        // Выполняем запрос
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw new CircuitBreakerException("Service unavailable");
        }
    }

    private void transitionTo(CircuitBreakerState newState) {
        this.state = newState;
        if (newState == CircuitBreakerState.HALF_OPENED) {
            successCount = 0;
        } else if (newState == CircuitBreakerState.CLOSED) {
            failureCount = 0;
            successCount = 0;
        }
    }

    private void onSuccess() {
        lock.lock();
        try {
            failureCount = 0;
            if (state == CircuitBreakerState.HALF_OPENED) {
                successCount++;
                if (successCount >= minSuccess) {
                    transitionTo(CircuitBreakerState.CLOSED);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void onFailure() {
        lock.lock();
        try {
            lastFailureTime = Instant.now();
            successCount = 0;
            if (state == CircuitBreakerState.HALF_OPENED) {
                failureCount = maxFailure;
                transitionTo(CircuitBreakerState.OPEN);
            } else {
                failureCount++;
                if (failureCount >= maxFailure) {
                    transitionTo(CircuitBreakerState.OPEN);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        lock.lock();
        try {
            transitionTo(CircuitBreakerState.CLOSED);
            lastFailureTime = null;
        } finally {
            lock.unlock();
        }
    }
}