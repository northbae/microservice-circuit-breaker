package kz.bmstu.kritinina.circuit_breaker;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "circuit-breaker.default-config")
public class CircuitBreakerProperties {
    private ServiceConfig defaultConfig = new ServiceConfig();
    private Map<String, ServiceConfig> services = new HashMap<>();

    @Data
    public static class ServiceConfig {
        private int maxFailure = 3;
        private int minSuccess = 1;

        @DurationUnit(ChronoUnit.SECONDS)
        private Duration timeout = Duration.ofSeconds(20);
    }
}