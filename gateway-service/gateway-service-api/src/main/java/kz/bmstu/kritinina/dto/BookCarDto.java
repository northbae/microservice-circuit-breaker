package kz.bmstu.kritinina.dto;

import jakarta.validation.constraints.NotNull;
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
public class BookCarDto {
    @NotNull
    private UUID carUid;

    @NotNull
    private LocalDate dateTo;

    @NotNull
    private LocalDate dateFrom;
}
