package kz.bmstu.kritinina.circuit_breaker;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Data
public class SlidingWindow {
    private final int size;
    private final AtomicReference<Measurement[]> measurements;
    private final AtomicInteger currentIndex;

    public SlidingWindow(int size) {
        this.size = size;
        this.measurements = new AtomicReference<>(new Measurement[size]);
        this.currentIndex = new AtomicInteger(0);
    }

    public void recordSuccess() {
        record(true);
    }

    public void recordFailure() {
        record(false);
    }

    private void record(boolean success) {
        int index = currentIndex.getAndUpdate(i -> (i + 1) % size);
        measurements.updateAndGet(arr -> {
            Measurement[] newArr = arr.clone();
            newArr[index] = new Measurement(success);
            return newArr;
        });
    }

    public Metrics getMetrics() {
        Measurement[] snapshot = measurements.get();
        int total = 0;
        int failures = 0;
        for (Measurement m : snapshot) {
            if (m != null) {
                total++;
                if (!m.isSuccess()) {
                    failures++;
                }
            }
        }
        return new Metrics(total, failures);
    }

    public void reset() {
        measurements.set(new Measurement[size]);
        currentIndex.set(0);
    }

    private static class Measurement {
        private final boolean success;

        public Measurement(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public static class Metrics {
        private final int totalCalls;
        private final int failedCalls;

        public Metrics(int totalCalls, int failedCalls) {
            this.totalCalls = totalCalls;
            this.failedCalls = failedCalls;
        }

        public int getTotalCalls() {
            return totalCalls;
        }

        public int getFailedCalls() {
            return failedCalls;
        }

        public int getSuccessfulCalls() {
            return totalCalls - failedCalls;
        }

        public float getFailureRate() {
            if (totalCalls == 0) {
                return 0;
            }
            return (float) failedCalls / totalCalls * 100;
        }
    }
}