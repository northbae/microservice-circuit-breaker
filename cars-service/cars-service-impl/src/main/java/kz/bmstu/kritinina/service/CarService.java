package kz.bmstu.kritinina.service;

import kz.bmstu.kritinina.dto.CarResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CarService {
    Page<CarResponse> getCars(Boolean showAll, Pageable pageable);

    List<CarResponse> getCars(List<UUID> carUids);

    void changeAvailability(UUID carId);

    CarResponse getCar(UUID carId);
}
