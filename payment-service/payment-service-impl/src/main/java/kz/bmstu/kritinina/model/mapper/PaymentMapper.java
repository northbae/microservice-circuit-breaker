package kz.bmstu.kritinina.model.mapper;

import kz.bmstu.kritinina.dto.PaymentResponse;
import kz.bmstu.kritinina.model.entity.Payment;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse toPaymentResponse(Payment payment);
}
