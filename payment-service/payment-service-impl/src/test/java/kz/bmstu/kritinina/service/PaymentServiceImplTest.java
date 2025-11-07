package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.dto.PaymentResponse;
import kz.bmstu.kritinina.exception.NotFoundException;
import kz.bmstu.kritinina.model.entity.Payment;
import kz.bmstu.kritinina.model.enums.PaymentStatus;
import kz.bmstu.kritinina.model.mapper.PaymentMapper;
import kz.bmstu.kritinina.repository.PaymentRepository;
import kz.bmstu.kritinina.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void createPayment_ShouldCreatePaymentWithCorrectParameters() {
        int price = 1000;
        UUID paymentUid = UUID.randomUUID();
        Payment payment = Payment.builder()
                .paymentUid(paymentUid)
                .status(PaymentStatus.PAID)
                .price(price)
                .build();
        Payment savedPayment = Payment.builder()
                .paymentUid(paymentUid)
                .status(PaymentStatus.PAID)
                .price(price)
                .build();
        PaymentResponse expectedResponse = new PaymentResponse();

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        when(paymentMapper.toPaymentResponse(savedPayment)).thenReturn(expectedResponse);

        PaymentResponse result = paymentService.createPayment(price);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(paymentRepository).save(argThat(p ->
                p.getStatus() == PaymentStatus.PAID &&
                        p.getPrice() == price &&
                        p.getPaymentUid() != null
        ));
        verify(paymentMapper).toPaymentResponse(savedPayment);
    }

    @Test
    void getPayments_ShouldReturnListOfPaymentResponses() {
        UUID paymentUid1 = UUID.randomUUID();
        UUID paymentUid2 = UUID.randomUUID();
        List<UUID> paymentUids = List.of(paymentUid1, paymentUid2);

        Payment payment1 = Payment.builder().paymentUid(paymentUid1).build();
        Payment payment2 = Payment.builder().paymentUid(paymentUid2).build();
        List<Payment> payments = List.of(payment1, payment2);

        PaymentResponse response1 = new PaymentResponse();
        PaymentResponse response2 = new PaymentResponse();
        List<PaymentResponse> expectedResponses = List.of(response1, response2);

        when(paymentRepository.findAllByPaymentUidIn(paymentUids)).thenReturn(payments);
        when(paymentMapper.toPaymentResponse(payment1)).thenReturn(response1);
        when(paymentMapper.toPaymentResponse(payment2)).thenReturn(response2);

        List<PaymentResponse> result = paymentService.getPayments(paymentUids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponses, result);
        verify(paymentRepository).findAllByPaymentUidIn(paymentUids);
        verify(paymentMapper, times(2)).toPaymentResponse(any(Payment.class));
    }

    @Test
    void getPayment_ShouldReturnPaymentResponse_WhenPaymentExists() {
        UUID paymentUid = UUID.randomUUID();
        Payment payment = Payment.builder().paymentUid(paymentUid).build();
        PaymentResponse expectedResponse = new PaymentResponse();

        when(paymentRepository.findByPaymentUid(paymentUid)).thenReturn(Optional.of(payment));
        when(paymentMapper.toPaymentResponse(payment)).thenReturn(expectedResponse);

        Optional<PaymentResponse> result = paymentService.getPayment(paymentUid);

        assertTrue(result.isPresent());
        assertEquals(expectedResponse, result.get());
        verify(paymentRepository).findByPaymentUid(paymentUid);
        verify(paymentMapper).toPaymentResponse(payment);
    }

    @Test
    void getPayment_ShouldThrowNotFoundException_WhenPaymentNotFound() {
        UUID paymentUid = UUID.randomUUID();

        when(paymentRepository.findByPaymentUid(paymentUid)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> paymentService.getPayment(paymentUid));

        verify(paymentRepository).findByPaymentUid(paymentUid);
        verify(paymentMapper, never()).toPaymentResponse(any());
    }

    @Test
    void cancelPayment_ShouldUpdatePaymentStatusToCanceled() {
        UUID paymentUid = UUID.randomUUID();
        Payment payment = Payment.builder()
                .paymentUid(paymentUid)
                .status(PaymentStatus.PAID)
                .price(1000)
                .build();

        when(paymentRepository.findByPaymentUid(paymentUid)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(payment)).thenReturn(payment);

        paymentService.cancelPayment(paymentUid);

        verify(paymentRepository).findByPaymentUid(paymentUid);
        verify(paymentRepository).save(payment);
        assertEquals(PaymentStatus.CANCELED, payment.getStatus());
    }
}
