package kz.bmstu.kritinina.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.bmstu.kritinina.dto.ErrorResponse;
import kz.bmstu.kritinina.dto.RentalRequest;
import kz.bmstu.kritinina.dto.RentalResponse;
import kz.bmstu.kritinina.dto.ValidationErrorResponse;
import kz.bmstu.kritinina.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RentalControllerImpl implements RentalController{
    private final RentalService rentalService;

    @Override
    public ResponseEntity<Void> getHealth() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<RentalResponse> getRentalById(UUID rentalUid, String username) {
        return ResponseEntity.ok(rentalService.getRentalById(rentalUid, username));
    }

    @Override
    public ResponseEntity<List<RentalResponse>> getAllRentals(String username) {
        return ResponseEntity.ok(rentalService.getAllRentals(username));
    }

    @Override
    public ResponseEntity<RentalResponse> createRental(String username, RentalRequest rentalRequest) {
        return ResponseEntity.ok(rentalService.createRental(username,rentalRequest));
    }

    @Override
    public ResponseEntity<Void> cancelRental(UUID rentalUid, String username) {
        rentalService.cancelRental(rentalUid, username);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> finishRental(UUID rentalUid, String username) {
        rentalService.finishRental(rentalUid, username);
        return ResponseEntity.ok().build();
    }
}
