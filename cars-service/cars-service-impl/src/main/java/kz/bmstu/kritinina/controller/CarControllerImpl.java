package kz.bmstu.kritinina.controller;

import kz.bmstu.kritinina.dto.CarResponse;
import kz.bmstu.kritinina.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CarControllerImpl implements CarController{
    private final CarService carService;

    @Override
    public ResponseEntity<Void> getHealth() {
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Page<CarResponse>> getCars(Boolean showAll, Pageable pageable) {
        return ResponseEntity.ok(carService.getCars(showAll, pageable));
    }

    @Override
    public ResponseEntity<List<CarResponse>> getListCars(List<UUID> carUids) {
        return ResponseEntity.ok(carService.getCars(carUids));
    }

    @Override
    public ResponseEntity<Void> changeAvailability(UUID carUid) {
        carService.changeAvailability(carUid);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CarResponse> getCar(UUID carUid) {
        return ResponseEntity.ok(carService.getCar(carUid));
    }
}
