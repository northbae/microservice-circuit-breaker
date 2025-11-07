package kz.bmstu.kritinina.client;

import kz.bmstu.kritinina.config.FeignConfig;
import kz.bmstu.kritinina.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "payment", url = "http://payment-service:8050/api/v1/payment", configuration = FeignConfig.class)
public interface PaymentClient {
    @PostMapping
    ResponseEntity<PaymentResponse> createPayment(@RequestParam("price") int price);

    //@GetMapping
    //ResponseEntity<List<PaymentResponse>> getPayments(@RequestParam("paymentsUids") List<UUID> paymentsUids);

    @PostMapping(value ="/{paymentUid}")
    ResponseEntity<Void> cancelPayment(@PathVariable("paymentUid") UUID paymentUid);

    @GetMapping(value ="/{paymentUid}")
    ResponseEntity<Optional<PaymentResponse>> getPayment(@PathVariable("paymentUid") UUID paymentUid);
}