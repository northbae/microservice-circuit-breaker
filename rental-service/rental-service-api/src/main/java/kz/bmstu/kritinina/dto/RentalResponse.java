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
public class RentalResponse {
    private Long id;
    private UUID rentalUid;
    private String username;
    private UUID paymentUid;
    private UUID carUid;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private RentalStatusDto status;
}
