package kz.bmstu.kritinina.circuit_breaker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
@Getter
public class CircuitBreaker {
    private final String name;
    private final CircuitBreakerConfig config;
    private final AtomicReference<CircuitBreakerState> state;
    private final SlidingWindow slidingWindow;
    private final AtomicLong stateTransitionTime;
    private final AtomicInteger halfOpenCallsCount;

    public CircuitBreaker(String name, CircuitBreakerConfig config) {
        this.name = name;
        this.config = config;
        this.state = new AtomicReference<>(CircuitBreakerState.CLOSED);
        this.slidingWindow = new SlidingWindow(config.getSlidingWindowSize());
        this.stateTransitionTime = new AtomicLong(System.currentTimeMillis());
        this.halfOpenCallsCount = new AtomicInteger(0);
    }

    public <T> T execute(Supplier<T> supplier) throws CircuitBreakerException {
        acquirePermission();

        long start = System.currentTimeMillis();
        try {
            T result = supplier.get();
            onSuccess(System.currentTimeMillis() - start);
            return result;
        } catch (Exception e) {
            onError(System.currentTimeMillis() - start);
            throw new CircuitBreakerException("");
        }
    }

    public void executeRunnable(Runnable runnable) throws CircuitBreakerException {
        execute(() -> {
            runnable.run();
            return null; });
    }

    private void acquirePermission() throws CircuitBreakerException {
        CircuitBreakerState currentState = state.get();

        if (currentState == CircuitBreakerState.OPEN) {
            if (shouldTransitionToHalfOpen()) {
                transitionToHalfOpen();
            } else {
                throw new CircuitBreakerException(
                        "CircuitBreaker '" + name + "' is OPEN"
                );
            }
        }

        if (currentState == CircuitBreakerState.HALF_OPENED) {
            if (halfOpenCallsCount.get() >=
                    config.getPermittedNumberOfCallsInHalfOpenState()) {
                throw new CircuitBreakerException(
                        "CircuitBreaker '" + name + "' is HALF_OPENED and max calls reached"
                );
            }
            halfOpenCallsCount.incrementAndGet();
        }
    }

    private void onSuccess(long duration) {
        CircuitBreakerState currentState = state.get();
        if (currentState == CircuitBreakerState.HALF_OPENED) {
            slidingWindow.recordSuccess();
            if (halfOpenCallsCount.get() >=
                    config.getPermittedNumberOfCallsInHalfOpenState()) {
                transitionToClosed();
            }
        } else if (currentState == CircuitBreakerState.CLOSED) {
            slidingWindow.recordSuccess();
        }
    }

    private void onError(long duration) {
        CircuitBreakerState currentState = state.get();

        if (currentState == CircuitBreakerState.HALF_OPENED) {
            transitionToOpen();
        } else if (currentState == CircuitBreakerState.CLOSED) {
            slidingWindow.recordFailure();
            checkFailureThreshold();
        }
    }

    private void checkFailureThreshold() {
        SlidingWindow.Metrics metrics = slidingWindow.getMetrics();
        if (metrics.getTotalCalls() < config.getMinimumNumberOfCalls()) {
            return;
        }
        if (metrics.getFailureRate() >= config.getFailureRateThreshold()) {
            transitionToOpen();
        }
    }

    private boolean shouldTransitionToHalfOpen() {
        long now = System.currentTimeMillis();
        long lastTransition = stateTransitionTime.get();
        return (now - lastTransition) >= config.getWaitDurationInOpenState();
    }

    private void transitionToOpen() {
        if (state.compareAndSet(CircuitBreakerState.CLOSED, CircuitBreakerState.OPEN) ||
                state.compareAndSet(CircuitBreakerState.HALF_OPENED, CircuitBreakerState.OPEN)) {
            stateTransitionTime.set(System.currentTimeMillis());
        }
    }

    private void transitionToHalfOpen() {
        if (state.compareAndSet(CircuitBreakerState.OPEN, CircuitBreakerState.HALF_OPENED)) {
            halfOpenCallsCount.set(0);
            stateTransitionTime.set(System.currentTimeMillis());
        }
    }

    private void transitionToClosed() {
        if (state.compareAndSet(CircuitBreakerState.HALF_OPENED, CircuitBreakerState.CLOSED)) {
            slidingWindow.reset();
            stateTransitionTime.set(System.currentTimeMillis());
        }
    }
}