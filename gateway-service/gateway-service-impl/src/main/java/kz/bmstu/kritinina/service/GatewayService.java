package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.config.CarPage;
import kz.bmstu.kritinina.dto.BookCarDto;
import kz.bmstu.kritinina.dto.CarDto;
import kz.bmstu.kritinina.dto.RentalCreationDto;
import kz.bmstu.kritinina.dto.RentalDto;
import kz.bmstu.kritinina.exception.ServiceUnavailableException;
import org.springframework.data.domain.Pageable;

import java.rmi.server.ServerCloneException;
import java.util.List;
import java.util.UUID;

public interface GatewayService {
    CarPage<CarDto> getAllCars(Boolean showAll, Pageable pageable) throws ServiceUnavailableException;

    List<RentalDto> getRental(String username);

    RentalDto getRental(String username, UUID rentalUid);

    RentalCreationDto bookCar(String userName, BookCarDto bookCarDto);

    void finishRental(String username, UUID rentalUid);

    void cancelRental(String username, UUID rentalUid);
}
