package kz.bmstu.kritinina.circuit_breaker;

public enum CircuitBreakerState {
    OPEN,
    CLOSED,
    HALF_OPENED
}
