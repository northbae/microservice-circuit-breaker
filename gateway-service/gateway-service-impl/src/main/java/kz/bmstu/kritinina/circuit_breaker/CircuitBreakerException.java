package kz.bmstu.kritinina.circuit_breaker;

public class CircuitBreakerException extends RuntimeException {
    public CircuitBreakerException(String message) {
        super(message);
    }
}