package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.dto.CarResponse;
import kz.bmstu.kritinina.exception.NotFoundException;
import kz.bmstu.kritinina.model.entity.Car;
import kz.bmstu.kritinina.model.mapper.CarMapper;
import kz.bmstu.kritinina.repository.CarRepository;
import kz.bmstu.kritinina.service.impl.CarServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(
        MockitoExtension.class
)
public class CarsServiceImplTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void changeAvailability_ShouldThrowNotFoundException_WhenCarNotFound() {
        UUID carUid = UUID.randomUUID();

        when(carRepository.findByCarUid(carUid)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> carService.changeAvailability(carUid));

        verify(carRepository).findByCarUid(carUid);
        verify(carRepository, never()).save(any());
    }

    @Test
    void getCars_ByUuidList_ShouldReturnListOfCarResponses() {
        UUID carUid1 = UUID.randomUUID();
        UUID carUid2 = UUID.randomUUID();
        List<UUID> carUuids = List.of(carUid1, carUid2);

        Car car1 = Car.builder().carUid(carUid1).build();
        Car car2 = Car.builder().carUid(carUid2).build();
        List<Car> cars = List.of(car1, car2);

        CarResponse response1 = new CarResponse();
        CarResponse response2 = new CarResponse();
        List<CarResponse> expectedResponses = List.of(response1, response2);

        when(carRepository.findAllByCarUidIn(carUuids)).thenReturn(cars);
        when(carMapper.toCarResponse(car1)).thenReturn(response1);
        when(carMapper.toCarResponse(car2)).thenReturn(response2);

        List<CarResponse> result = carService.getCars(carUuids);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponses, result);
        verify(carRepository).findAllByCarUidIn(carUuids);
        verify(carMapper, times(2)).toCarResponse(any(Car.class));
    }

    @Test
    void changeAvailability_ShouldToggleCarAvailability() {
        UUID carUid = UUID.randomUUID();
        Car car = Car.builder()
                .carUid(carUid)
                .availability(true)
                .build();

        when(carRepository.findByCarUid(carUid)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);

        carService.changeAvailability(carUid);

        assertFalse(car.isAvailability());
        verify(carRepository).findByCarUid(carUid);
        verify(carRepository).save(car);

        carService.changeAvailability(carUid);

        assertTrue(car.isAvailability());
        verify(carRepository, times(2)).findByCarUid(carUid);
        verify(carRepository, times(2)).save(car);
    }

    @Test
    void getCar_ShouldReturnCarResponse_WhenCarExists() {
        UUID carUid = UUID.randomUUID();
        Car car = Car.builder().carUid(carUid).build();
        CarResponse expectedResponse = new CarResponse();

        when(carRepository.findByCarUid(carUid)).thenReturn(Optional.of(car));
        when(carMapper.toCarResponse(car)).thenReturn(expectedResponse);

        CarResponse result = carService.getCar(carUid);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(carRepository).findByCarUid(carUid);
        verify(carMapper).toCarResponse(car);
    }
}
