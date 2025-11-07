package kz.bmstu.kritinina.controller;

import kz.bmstu.kritinina.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("api/v1")
public interface PaymentController {
    @GetMapping("/manage/health")
    ResponseEntity<Void> getHealth();

    @PostMapping("/payment")
    ResponseEntity<PaymentResponse> createPayment(@RequestParam int price);

    @GetMapping("/payment")
    ResponseEntity<List<PaymentResponse>> getPayments(@RequestParam List<UUID> paymentsUids);

    @PostMapping("/payment/{paymentUid}")
    ResponseEntity<Void> cancelPayment(@PathVariable("paymentUid") UUID paymentUid);

    @GetMapping("/payment/{paymentUid}")
    ResponseEntity<Optional<PaymentResponse>> getPayment(@PathVariable("paymentUid") UUID paymentUid);
}
