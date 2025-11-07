package kz.bmstu.kritinina.saga;

public class SagaException extends RuntimeException {
    public SagaException(String message) {
        super(message);
    }
}