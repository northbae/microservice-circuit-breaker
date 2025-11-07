package kz.bmstu.kritinina.controller;

import kz.bmstu.kritinina.dto.PaymentResponse;
import kz.bmstu.kritinina.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController{
    private final PaymentService paymentService;

    @Override
    public ResponseEntity<Void> getHealth() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<PaymentResponse> createPayment(int price) {
        return ResponseEntity.ok(paymentService.createPayment(price));
    }

    @Override
    public ResponseEntity<List<PaymentResponse>> getPayments(List<UUID> paymentsUids) {
        return ResponseEntity.ok(paymentService.getPayments(paymentsUids));
    }

    @Override
    public ResponseEntity<Void> cancelPayment(UUID paymentUid) {
        paymentService.cancelPayment(paymentUid);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Optional<PaymentResponse>> getPayment(UUID paymentUid) {
        return ResponseEntity.ok(paymentService.getPayment(paymentUid));
    }
}
