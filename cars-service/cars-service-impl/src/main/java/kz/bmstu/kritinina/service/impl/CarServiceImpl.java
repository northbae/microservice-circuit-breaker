package kz.bmstu.kritinina.service.impl;

import kz.bmstu.kritinina.dto.CarResponse;
import kz.bmstu.kritinina.exception.NotFoundException;
import kz.bmstu.kritinina.model.entity.Car;
import kz.bmstu.kritinina.model.mapper.CarMapper;
import kz.bmstu.kritinina.repository.CarRepository;
import kz.bmstu.kritinina.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public Page<CarResponse> getCars(Boolean showAll, Pageable pageable) {
        Pageable adjustedPageable = PageRequest.of(
                Math.max(0, pageable.getPageNumber() - 1),
                pageable.getPageSize(),
                pageable.getSort()
        );
        Page<Car> cars = Boolean.TRUE.equals(showAll) ? carRepository.findAll(adjustedPageable) :
                carRepository.findAllByAvailabilityTrue(adjustedPageable);
        return cars.map(carMapper::toCarResponse);
    }

    @Override
    public List<CarResponse> getCars(List<UUID> carUuids) {
        return carRepository.findAllByCarUidIn(carUuids).stream().map(carMapper::toCarResponse).toList();
    }

    @Override
    public void changeAvailability(UUID carUid) {
        Car car = carRepository.findByCarUid(carUid).orElseThrow(() ->
                new NotFoundException("Car with id {carUid} is not found"));
        car.setAvailability(!car.isAvailability());
        carRepository.save(car);
    }

    @Override
    public CarResponse getCar(UUID carUid) {
        return carRepository.findByCarUid(carUid).map(carMapper::toCarResponse).orElseThrow(() ->
                new NotFoundException("Car with id {carUid} is not found"));
    }
}
