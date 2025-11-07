package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.dto.RentalRequest;
import kz.bmstu.kritinina.dto.RentalResponse;
import kz.bmstu.kritinina.exception.NotFoundException;
import kz.bmstu.kritinina.model.entity.Rental;
import kz.bmstu.kritinina.model.enums.RentalStatus;
import kz.bmstu.kritinina.model.mapper.RentalMapper;
import kz.bmstu.kritinina.repository.RentalRepository;
import kz.bmstu.kritinina.service.impl.RentalServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalServiceImpl rentalService;

    private final String USERNAME = "testUser";
    private final UUID RENTAL_UID = UUID.randomUUID();

    @Test
    void getRentalById_ShouldReturnRentalResponse_WhenRentalExists() {
        Rental rental = new Rental();
        RentalResponse expectedResponse = new RentalResponse();

        when(rentalRepository.findByUsernameAndRentalUid(USERNAME, RENTAL_UID))
                .thenReturn(Optional.of(rental));
        when(rentalMapper.toRentalResponse(rental)).thenReturn(expectedResponse);

        RentalResponse actualResponse = rentalService.getRentalById(RENTAL_UID, USERNAME);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        verify(rentalRepository).findByUsernameAndRentalUid(USERNAME, RENTAL_UID);
        verify(rentalMapper).toRentalResponse(rental);
    }

    @Test
    void getRentalById_ShouldThrowNotFoundException_WhenRentalNotFound() {
        when(rentalRepository.findByUsernameAndRentalUid(USERNAME, RENTAL_UID))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> rentalService.getRentalById(RENTAL_UID, USERNAME));

        assertEquals("Аренда не найдена", exception.getMessage());
        verify(rentalRepository).findByUsernameAndRentalUid(USERNAME, RENTAL_UID);
        verify(rentalMapper, never()).toRentalResponse(any());
    }

    @Test
    void createRental_ShouldCreateAndReturnRentalResponse() {
        RentalRequest rentalRequest = new RentalRequest();
        Rental rental = new Rental();
        Rental savedRental = new Rental();
        RentalResponse expectedResponse = new RentalResponse();

        when(rentalMapper.toRental(rentalRequest)).thenReturn(rental);
        when(rentalRepository.save(rental)).thenReturn(savedRental);
        when(rentalMapper.toRentalResponse(savedRental)).thenReturn(expectedResponse);

        RentalResponse actualResponse = rentalService.createRental(USERNAME, rentalRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        verify(rentalMapper).toRental(rentalRequest);
        verify(rentalRepository).save(rental);
        verify(rentalMapper).toRentalResponse(savedRental);
    }

    @Test
    void cancelRental_ShouldChangeStatusToCanceled() {
        Rental rental = mock(Rental.class);

        when(rentalRepository.findByUsernameAndRentalUid(USERNAME, RENTAL_UID))
                .thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);

        rentalService.cancelRental(RENTAL_UID, USERNAME);

        verify(rentalRepository).findByUsernameAndRentalUid(USERNAME, RENTAL_UID);
        verify(rental).setStatus(RentalStatus.CANCELED);
        verify(rentalRepository).save(rental);
    }

    @Test
    void finishRental_ShouldChangeStatusToFinished() {
        Rental rental = mock(Rental.class);

        when(rentalRepository.findByUsernameAndRentalUid(USERNAME, RENTAL_UID))
                .thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);

        rentalService.finishRental(RENTAL_UID, USERNAME);

        verify(rentalRepository).findByUsernameAndRentalUid(USERNAME, RENTAL_UID);
        verify(rental).setStatus(RentalStatus.FINISHED);
        verify(rentalRepository).save(rental);
    }
}
