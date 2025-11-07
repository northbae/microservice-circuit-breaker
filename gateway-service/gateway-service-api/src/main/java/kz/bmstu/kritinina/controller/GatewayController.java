package kz.bmstu.kritinina.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import kz.bmstu.kritinina.config.CarPage;
import kz.bmstu.kritinina.dto.BookCarDto;
import kz.bmstu.kritinina.dto.CarDto;
import kz.bmstu.kritinina.dto.RentalCreationDto;
import kz.bmstu.kritinina.dto.RentalDto;
import kz.bmstu.kritinina.dto.ServiceError;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("api/v1")
public interface GatewayController {
    public static final String USERNAME_HEADER = "X-User-Name";

    @GetMapping("/manage/health")
    ResponseEntity<Void> getHealth();

    @ApiResponse(responseCode = "200", description = "Список доступных для бронирования автомобилей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список доступных для бронирования автомобилей"),
            @ApiResponse(responseCode = "500", description = "Сервис недоступен",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ServiceError.class)) })
    })
    @GetMapping("/cars")
    CarPage<CarDto> getAllCars(@RequestParam Boolean showAll,
                               @ParameterObject Pageable pageable);

    @ApiResponse(responseCode = "200", description = "Список доступных для бронирования автомобилей")
    @GetMapping("/rental")
    ResponseEntity<List<RentalDto>> getRental(@RequestHeader(USERNAME_HEADER) String username);

    @GetMapping("/rental/{rentalUid}")
    ResponseEntity<RentalDto> getRental(@RequestHeader(USERNAME_HEADER) String username,
                                        @PathVariable("rentalUid") UUID rentalUid);

    @PostMapping("/rental")
    ResponseEntity<RentalCreationDto> bookCar(@RequestHeader(USERNAME_HEADER) String username,
                                              @RequestBody BookCarDto bookCarDto);

    @PostMapping("/rental/{rentalUid}/finish")
    ResponseEntity<Void> finishRental(@RequestHeader(USERNAME_HEADER) String username,
                                   @PathVariable("rentalUid") UUID rentalUid);

    @DeleteMapping("/rental/{rentalUid}")
    @ApiResponse(responseCode = "204", description = "Аренда успешно отменена")
    ResponseEntity<Void> cancelRental(@RequestHeader(USERNAME_HEADER) String username,
                                   @PathVariable("rentalUid") UUID rentalUid);
}
