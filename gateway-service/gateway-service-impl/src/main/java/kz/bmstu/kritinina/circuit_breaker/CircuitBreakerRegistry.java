package kz.bmstu.kritinina.circuit_breaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class CircuitBreakerRegistry {
    private final ConcurrentHashMap<String, CircuitBreaker> circuitBreakers =
            new ConcurrentHashMap<>();
    private final CircuitBreakerConfig defaultConfig;

    public CircuitBreakerRegistry() {
        this.defaultConfig = CircuitBreakerConfig.builder()
                .slidingWindowSize(100)
                .failureRateThreshold(50)
                .minimumNumberOfCalls(10)
                .waitDurationInOpenState(60000)
                .permittedNumberOfCallsInHalfOpenState(10)
                .build();
    }

    public CircuitBreaker getOrCreate(String name) {
        return circuitBreakers.computeIfAbsent(
                name, k -> new CircuitBreaker(k, defaultConfig));
    }

    public CircuitBreaker getOrCreate(String name, CircuitBreakerConfig config) {
        return circuitBreakers.computeIfAbsent(
                name, k -> new CircuitBreaker(k, config));
    }

    public CircuitBreaker get(String name) {
        return circuitBreakers.get(name);
    }
}