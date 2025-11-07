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
public interface RentalController {
    public static final String USERNAME_HEADER = "X-User-Name";

    @GetMapping("/manage/health")
    ResponseEntity<Void> getHealth();

    @Operation(summary = "Информация по конкретной аренде пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация по конкретному бронированию",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RentalResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Билет не найден",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @GetMapping("/rental/{rentalUid}")
    ResponseEntity<RentalResponse> getRentalById(@PathVariable("rentalUid") UUID rentalUid, @RequestHeader(USERNAME_HEADER) String username);

    @Operation(summary = "Получить информацию о всех арендах пользователя")
    @ApiResponse(responseCode = "200", description = "Информация обо всех арендах",
            content = {@Content(mediaType = APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = RentalResponse.class))})
    @GetMapping("/rental")
    ResponseEntity<List<RentalResponse>> getAllRentals(@RequestHeader(USERNAME_HEADER) String username);

    @Operation(summary = "Забронировать автомобиль")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Информация о бронировании авто",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RentalResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ValidationErrorResponse.class)) })
    })
    @PostMapping("/rental")
    ResponseEntity<RentalResponse> createRental(@RequestHeader(USERNAME_HEADER) String username, @RequestBody RentalRequest rentalRequest);

    @Operation(summary = "Завершение аренды автомобиля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Аренда успешно завершена"),
            @ApiResponse(responseCode = "404", description = "Аренда не найдена",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @PostMapping("/rental/{rentalUid}/finish")
    ResponseEntity<Void> finishRental(@PathVariable("rentalUid") UUID rentalUid, @RequestHeader(USERNAME_HEADER) String username);

    @Operation(summary = "Отмена аренды автомобиля")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Аренда успешно отменена"),
            @ApiResponse(responseCode = "404", description = "Аренда не найдена",
                    content = { @Content(mediaType = APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)) })
    })
    @DeleteMapping("/rental/{rentalUid}")
    ResponseEntity<Void> cancelRental(@PathVariable("rentalUid") UUID rentalUid, @RequestHeader(USERNAME_HEADER) String username);
}
