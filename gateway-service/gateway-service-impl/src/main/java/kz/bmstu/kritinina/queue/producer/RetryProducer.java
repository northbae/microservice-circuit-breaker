package kz.bmstu.kritinina.queue.producer;

import kz.bmstu.kritinina.queue.config.RabbitMqConfig;
import kz.bmstu.kritinina.queue.message.RetryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendToRetryQueue(String operationType, Map<String, Object> payload,
                                 String failureReason) {

        RetryMessage message = RetryMessage.builder()
                .operationType(operationType)
                .payload(payload)
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .failureReason(failureReason)
                .build();

        log.info("Sending message to retry queue: {}", operationType);

        rabbitTemplate.convertAndSend(
                RabbitMqConfig.RETRY_EXCHANGE,
                RabbitMqConfig.RETRY_ROUTING_KEY,
                message
        );
    }
}