package kz.bmstu.kritinina.config;

import kz.bmstu.kritinina.client.CarClient;
import kz.bmstu.kritinina.client.PaymentClient;
import kz.bmstu.kritinina.client.RentalClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {CarClient.class, RentalClient.class, PaymentClient.class}, defaultConfiguration = FeignConfig.class)
public class FeignConfig {
}