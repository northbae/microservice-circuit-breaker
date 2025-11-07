package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.dto.RentalRequest;
import kz.bmstu.kritinina.dto.RentalResponse;

import java.util.List;
import java.util.UUID;

public interface RentalService {
    RentalResponse getRentalById(UUID rentalUid, String username);
    List<RentalResponse> getAllRentals(String username);
    RentalResponse createRental(String username, RentalRequest person);
    void cancelRental(UUID rentalUid, String username);
    void finishRental(UUID rentalUid, String username);
}
