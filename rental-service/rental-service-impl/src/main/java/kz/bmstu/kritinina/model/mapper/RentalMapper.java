package kz.bmstu.kritinina.model.mapper;

import kz.bmstu.kritinina.dto.RentalRequest;
import kz.bmstu.kritinina.dto.RentalResponse;
import kz.bmstu.kritinina.model.entity.Rental;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    Rental toRental(RentalRequest rentalRequest);

    RentalResponse toRentalResponse(Rental rental);
}
