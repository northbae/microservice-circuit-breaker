package kz.bmstu.kritinina.service.impl;

import kz.bmstu.kritinina.dto.RentalRequest;
import kz.bmstu.kritinina.dto.RentalResponse;
import kz.bmstu.kritinina.exception.NotFoundException;
import kz.bmstu.kritinina.model.entity.Rental;
import kz.bmstu.kritinina.model.enums.RentalStatus;
import kz.bmstu.kritinina.model.mapper.RentalMapper;
import kz.bmstu.kritinina.repository.RentalRepository;
import kz.bmstu.kritinina.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;

    @Override
    public RentalResponse getRentalById(UUID rentalUid, String username) {
        return rentalRepository.findByUsernameAndRentalUid(username, rentalUid).map
                (rentalMapper::toRentalResponse).orElseThrow(() -> new NotFoundException("Аренда не найдена"));
    }

    @Override
    public List<RentalResponse> getAllRentals(String username) {
        return rentalRepository.findAllByUsername(username).stream().map(rentalMapper::toRentalResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RentalResponse createRental(String username, RentalRequest rentalRequest) {
        Rental rental = rentalMapper.toRental(rentalRequest);
        rental.setUsername(username);
        rental.setStatus(RentalStatus.IN_PROGRESS);
        rental.setRentalUid(UUID.randomUUID());
        return rentalMapper.toRentalResponse(rentalRepository.save(rental));
    }

    @Override
    public void cancelRental(UUID rentalUid, String username) {
        changeRentalStatus(username, rentalUid, RentalStatus.CANCELED);
    }

    @Override
    public void finishRental(UUID rentalUid, String username) {
        changeRentalStatus(username, rentalUid, RentalStatus.FINISHED);
    }

    private void changeRentalStatus(String username, UUID rentalUid, RentalStatus rentalStatus) {
        Rental rental = rentalRepository.findByUsernameAndRentalUid(username, rentalUid).
                orElseThrow(() -> new NotFoundException("Аренда не найдена"));
        rental.setStatus(rentalStatus);
        rentalRepository.save(rental);
    }
}
