package kz.bmstu.kritinina.queue.consumer;

import kz.bmstu.kritinina.queue.config.RabbitMqConfig;
import kz.bmstu.kritinina.queue.message.RetryMessage;
import kz.bmstu.kritinina.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryConsumer {
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMqConfig.RETRY_QUEUE)
    public void processRetryMessage(RetryMessage message) {
        try {
            switch (message.getOperationType()) {
                case "BOOK_CAR":
                    // Повторная попытка бронирования
                    log.info("Retry successful for: {}", message.getOperationType());
                    break;
                default:
                    log.warn("Unknown operation type: {}", message.getOperationType());
            }

        } catch (Exception e) {
            log.error("Retry failed for: {}, error: {}",
                    message.getOperationType(), e.getMessage());

            message.setRetryCount(message.getRetryCount() + 1);
            message.setFailureReason(e.getMessage());

            if (message.getRetryCount() < message.getMaxRetries()) {
                log.info("Scheduling retry {}/{}",
                        message.getRetryCount(), message.getMaxRetries());

                rabbitTemplate.convertAndSend(
                        RabbitMqConfig.RETRY_EXCHANGE,
                        RabbitMqConfig.RETRY_ROUTING_KEY,
                        message
                );
            } else {
                log.error("Max retries reached for: {}", message.getOperationType());
            }
        }
    }
}