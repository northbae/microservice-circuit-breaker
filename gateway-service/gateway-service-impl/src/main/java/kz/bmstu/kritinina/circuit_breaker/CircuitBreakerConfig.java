package kz.bmstu.kritinina.circuit_breaker;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CircuitBreakerConfig {
    private final int slidingWindowSize;
    private final int failureRateThreshold;
    private final int minimumNumberOfCalls;
    private final long waitDurationInOpenState;
    private final int permittedNumberOfCallsInHalfOpenState;

    public CircuitBreakerConfig(int slidingWindowSize,
                                int failureRateThreshold,
                                int minimumNumberOfCalls,
                                long waitDurationInOpenState,
                                int permittedNumberOfCallsInHalfOpenState) {
        this.slidingWindowSize = slidingWindowSize;
        this.failureRateThreshold = failureRateThreshold;
        this.minimumNumberOfCalls = minimumNumberOfCalls;
        this.waitDurationInOpenState = waitDurationInOpenState;
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int slidingWindowSize = 100;
        private int failureRateThreshold = 50;
        private int minimumNumberOfCalls = 10;
        private long waitDurationInOpenState = 60000;
        private int permittedNumberOfCallsInHalfOpenState = 10;

        public Builder slidingWindowSize(int size) {
            this.slidingWindowSize = size;
            return this;
        }

        public Builder failureRateThreshold(int threshold) {
            this.failureRateThreshold = threshold;
            return this;
        }

        public Builder minimumNumberOfCalls(int calls) {
            this.minimumNumberOfCalls = calls;
            return this;
        }

        public Builder waitDurationInOpenState(long duration) {
            this.waitDurationInOpenState = duration;
            return this;
        }

        public Builder permittedNumberOfCallsInHalfOpenState(int calls) {
            this.permittedNumberOfCallsInHalfOpenState = calls;
            return this;
        }

        public CircuitBreakerConfig build() {
            return new CircuitBreakerConfig(
                    slidingWindowSize,
                    failureRateThreshold,
                    minimumNumberOfCalls,
                    waitDurationInOpenState,
                    permittedNumberOfCallsInHalfOpenState
            );
        }
    }
}