package kz.bmstu.kritinina.queue.consumer;

import kz.bmstu.kritinina.client.PaymentClient;
import kz.bmstu.kritinina.client.RentalClient;
import kz.bmstu.kritinina.queue.config.RabbitMqConfig;
import kz.bmstu.kritinina.queue.message.RetryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryConsumer {
    private final RabbitTemplate rabbitTemplate;
    private final RentalClient rentalClient;
    private final PaymentClient paymentClient;

    @RabbitListener(queues = RabbitMqConfig.RETRY_QUEUE)
    public void processRetryMessage(RetryMessage message) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            switch (message.getOperationType()) {
                case "FINISH_RENTAL":
                    handleFinishRental(message);
                    break;
                case "CANCEL_RENTAL":
                    handleCancelRental(message);
                    break;
                case "CANCEL_PAYMENT":
                    handleCancelPayment(message);
                    break;
                default:
                    log.warn("Unknown operation type: {}", message.getOperationType());
            }

        } catch (Exception e) {
            message.setRetryCount(message.getRetryCount() + 1);
            message.setFailureReason(e.getMessage());
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.RETRY_EXCHANGE,
                    RabbitMqConfig.RETRY_ROUTING_KEY,
                    message
            );
        }
    }

    private void handleFinishRental(RetryMessage message) {
        UUID rentalUid = UUID.fromString((String) message.getPayload().get("rentalUid"));
        String username = (String) message.getPayload().get("username");
        rentalClient.finishRental(rentalUid, username);
    }

    private void handleCancelRental(RetryMessage message) {
        UUID rentalUid = UUID.fromString((String) message.getPayload().get("rentalUid"));
        String username = (String) message.getPayload().get("username");
        rentalClient.cancelRental(rentalUid, username);
    }

    private void handleCancelPayment(RetryMessage message) {
        UUID paymentUid = UUID.fromString((String) message.getPayload().get("paymentUid"));
        paymentClient.cancelPayment(paymentUid);
    }
}