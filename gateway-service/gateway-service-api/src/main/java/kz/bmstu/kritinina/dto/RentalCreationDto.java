package kz.bmstu.kritinina.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalCreationDto {
    private UUID rentalUid;
    private RentalStatus status;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private UUID carUid;
    private PaymentDto payment;
}
