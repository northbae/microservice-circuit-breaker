package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.dto.PaymentResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentService {
    PaymentResponse createPayment(int price);

    List<PaymentResponse> getPayments(List<UUID> paymentsUids);

    void cancelPayment(UUID paymentUid);

    Optional<PaymentResponse> getPayment(UUID paymentUid);
}
