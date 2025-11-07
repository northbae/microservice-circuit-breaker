package kz.bmstu.kritinina.circuit_breaker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerRegistry {

    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    private final CircuitBreakerProperties properties;

    public CircuitBreaker getOrCreate(String serviceName) {
        return circuitBreakers.computeIfAbsent(serviceName, this::createCircuitBreaker);
    }

    private CircuitBreaker createCircuitBreaker(String serviceName) {
        CircuitBreakerProperties.ServiceConfig config =
                properties.getServices().getOrDefault(serviceName, properties.getDefaultConfig());
        return new CircuitBreaker(
                serviceName,
                config.getMaxFailure(),
                config.getMinSuccess(),
                config.getTimeout()
        );
    }
}