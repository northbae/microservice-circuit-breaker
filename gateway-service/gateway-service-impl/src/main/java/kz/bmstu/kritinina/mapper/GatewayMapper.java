package kz.bmstu.kritinina.mapper;

import kz.bmstu.kritinina.config.CarPage;
import kz.bmstu.kritinina.dto.CarBaseDto;
import kz.bmstu.kritinina.dto.CarDto;
import kz.bmstu.kritinina.dto.CarResponse;
import kz.bmstu.kritinina.dto.PaymentDto;
import kz.bmstu.kritinina.dto.PaymentResponse;
import kz.bmstu.kritinina.dto.RentalCreationDto;
import kz.bmstu.kritinina.dto.RentalDto;
import kz.bmstu.kritinina.dto.RentalResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface GatewayMapper {
    RentalDto toRentalDto(RentalResponse rentalResponse);

    RentalCreationDto toRentalCreationDto(RentalResponse rentalResponse);

    @Mapping(source = "carType", target = "type")
    @Mapping(source = "availability", target = "available")
    CarDto toCarDto(CarResponse carResponse);

    CarBaseDto toCarBaseDto(CarResponse carResponse);

    PaymentDto toPaymentDto(PaymentResponse paymentResponse);

    CarPage<CarDto> toCarPage(Page<CarDto> carDto);
}
