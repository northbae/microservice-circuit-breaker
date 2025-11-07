package kz.bmstu.kritinina.controller;

import kz.bmstu.kritinina.config.CarPage;
import kz.bmstu.kritinina.dto.BookCarDto;
import kz.bmstu.kritinina.dto.CarDto;
import kz.bmstu.kritinina.dto.RentalCreationDto;
import kz.bmstu.kritinina.dto.RentalDto;
import kz.bmstu.kritinina.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GatewayControllerImpl implements GatewayController{
    private final GatewayService gatewayService;

    @Override
    public ResponseEntity<Void> getHealth() {
        return ResponseEntity.ok().build();
    }

    @Override
    public CarPage<CarDto> getAllCars(Boolean showAll, Pageable pageable) {
        return gatewayService.getAllCars(showAll, pageable);
    }

    @Override
    public ResponseEntity<List<RentalDto>> getRental(String username) {
        return ResponseEntity.ok(gatewayService.getRental(username));
    }

    @Override
    public ResponseEntity<RentalDto> getRental( String username, UUID rentalUid) {
        return ResponseEntity.ok(gatewayService.getRental(username, rentalUid));
    }

    @Override
    public ResponseEntity<RentalCreationDto> bookCar(String username, BookCarDto bookCarDto) {
        return ResponseEntity.ok(gatewayService.bookCar(username, bookCarDto));
    }

    @Override
    public ResponseEntity<Void> finishRental(String username, UUID rentalUid) {
        gatewayService.finishRental(username, rentalUid);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> cancelRental(String username, UUID rentalUid) {
        gatewayService.cancelRental(username, rentalUid);
        return ResponseEntity.noContent().build();
    }
}
