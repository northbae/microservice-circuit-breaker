package kz.bmstu.kritinina.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalRequest {
    private UUID carUid;
    private UUID paymentUid;
    private LocalDate dateFrom;
    private LocalDate dateTo;
}
