package kz.bmstu.kritinina.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kz.bmstu.kritinina.dto.CarResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequestMapping("api/v1")
public interface CarController {

    @GetMapping("/manage/health")
    ResponseEntity<Void> getHealth();

    @ApiResponse(responseCode = "200", description = "Список доступных для бронирования автомобилей")
    @GetMapping("/cars")
    ResponseEntity<Page<CarResponse>> getCars(@RequestParam Boolean showAll, @ParameterObject Pageable pageable);

    @PostMapping("/cars")
    ResponseEntity<List<CarResponse>> getListCars(@RequestParam List<UUID> carUids);

    @PostMapping("/cars/{carUid}")
    ResponseEntity<Void> changeAvailability(@PathVariable("carUid") UUID carUid);

    @GetMapping("/cars/{carUid}")
    ResponseEntity<CarResponse> getCar(@PathVariable("carUid") UUID carUid);
}
