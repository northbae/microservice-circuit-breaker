package kz.bmstu.kritinina.service.impl;

import kz.bmstu.kritinina.dto.PaymentResponse;
import kz.bmstu.kritinina.exception.NotFoundException;
import kz.bmstu.kritinina.model.entity.Payment;
import kz.bmstu.kritinina.model.enums.PaymentStatus;
import kz.bmstu.kritinina.model.mapper.PaymentMapper;
import kz.bmstu.kritinina.repository.PaymentRepository;
import kz.bmstu.kritinina.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentResponse createPayment(int price) {
        Payment payment = Payment.builder()
                .paymentUid(UUID.randomUUID())
                .status(PaymentStatus.PAID)
                .price(price)
                .build();
        return paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponse> getPayments(List<UUID> paymentsUids) {
        return paymentRepository.findAllByPaymentUidIn(paymentsUids).stream().map(paymentMapper::toPaymentResponse).toList();
    }

    @Override
    public Optional<PaymentResponse> getPayment(UUID paymentUid) {
        return Optional.of(paymentRepository.findByPaymentUid(paymentUid).map(paymentMapper::toPaymentResponse).orElseThrow(() ->
                new NotFoundException("Payment with id {paymentUid} not found")));
    }

    @Override
    public void cancelPayment(UUID paymentUid) {
        Payment payment = paymentRepository.findByPaymentUid(paymentUid).orElseThrow(() ->
                new NotFoundException("Payment with id {paymentUid} not found"));
        payment.setStatus(PaymentStatus.CANCELED);
        paymentRepository.save(payment);
    }
}
