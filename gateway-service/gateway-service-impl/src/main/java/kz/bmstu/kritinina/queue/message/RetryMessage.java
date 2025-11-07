package kz.bmstu.kritinina.queue.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryMessage {
    private String operationType;
    private Map<String, Object> payload;
    private int retryCount;
    private int maxRetries;
    private LocalDateTime createdAt;
    private String failureReason;
}