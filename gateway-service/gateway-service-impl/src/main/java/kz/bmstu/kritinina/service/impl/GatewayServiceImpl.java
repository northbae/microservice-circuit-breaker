package kz.bmstu.kritinina.service.impl;

import kz.bmstu.kritinina.circuit_breaker.CircuitBreaker;
import kz.bmstu.kritinina.circuit_breaker.CircuitBreakerException;
import kz.bmstu.kritinina.circuit_breaker.CircuitBreakerRegistry;
import kz.bmstu.kritinina.client.CarClient;
import kz.bmstu.kritinina.client.PaymentClient;
import kz.bmstu.kritinina.client.RentalClient;
import kz.bmstu.kritinina.config.CarPage;
import kz.bmstu.kritinina.dto.BookCarDto;
import kz.bmstu.kritinina.dto.CarBaseDto;
import kz.bmstu.kritinina.dto.CarDto;
import kz.bmstu.kritinina.dto.CarResponse;
import kz.bmstu.kritinina.dto.PaymentDto;
import kz.bmstu.kritinina.dto.PaymentResponse;
import kz.bmstu.kritinina.dto.RentalCreationDto;
import kz.bmstu.kritinina.dto.RentalDto;
import kz.bmstu.kritinina.dto.RentalRequest;
import kz.bmstu.kritinina.dto.RentalResponse;
import kz.bmstu.kritinina.exception.InvalidOperationException;
import kz.bmstu.kritinina.exception.ServiceUnavailableException;
import kz.bmstu.kritinina.mapper.GatewayMapper;
import kz.bmstu.kritinina.queue.producer.RetryProducer;
import kz.bmstu.kritinina.saga.SagaContext;
import kz.bmstu.kritinina.saga.SagaException;
import kz.bmstu.kritinina.saga.SagaOrchestrator;
import kz.bmstu.kritinina.saga.SagaStep;
import kz.bmstu.kritinina.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Period;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayServiceImpl implements GatewayService {
    private static final Logger log = LoggerFactory.getLogger(GatewayServiceImpl.class);
    private final CarClient carClient;
    private final PaymentClient paymentClient;
    private final RentalClient rentalClient;
    private final GatewayMapper gatewayMapper;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final SagaOrchestrator sagaOrchestrator;
    private final RetryProducer retryProducer;

    private final String ANSWER_FOR_SERVICE_UNAVAILABLE = "Сервис недоступен";

    @Override
    public CarPage<CarDto> getAllCars(Boolean showAll, Pageable pageable)  {
        int originalPage = pageable.getPageNumber();
        Pageable adjustedPageable = PageRequest.of(
                Math.max(0, originalPage - 1),
                pageable.getPageSize(),
                Sort.unsorted()
        );
        try {
            Page<CarDto> result = getAllCarsWithCircuitBreaker(showAll, adjustedPageable);
            return gatewayMapper.toCarPage(result);
        }
        catch (CircuitBreakerException e) {
            throw new ServiceUnavailableException("Cars Service unavailable");
        }
    }

    @Override
    public List<RentalDto> getRental(String username) {
        List<RentalResponse> rentalResponses = getRentalResponsesWithCircuitBreaker(username);
        List<RentalDto> rentalDtos = rentalResponses.stream().map(gatewayMapper::toRentalDto).toList();
        for (int i = 0; i < rentalDtos.size(); i++) {
            PaymentDto paymentDto = getPaymentWithFallback(rentalResponses.get(i).getPaymentUid());
            rentalDtos.get(i).setPayment(paymentDto);
            CarBaseDto carBaseDto = getCarWithFallback(rentalResponses.get(i).getCarUid());
            rentalDtos.get(i).setCar(carBaseDto);
        }
        return rentalDtos;
    }

    @Override
    public RentalDto getRental(String username, UUID rentalUid) {
        RentalResponse rentalResponse = getRentalByIdWithCircuitBreaker(rentalUid, username);
        CarBaseDto carBaseDto = getCarWithFallback(rentalResponse.getCarUid());
        try {
            PaymentDto paymentDto = getPaymentWithFallback(rentalResponse.getPaymentUid());
            RentalDto rentalDto = gatewayMapper.toRentalDto(rentalResponse);
            rentalDto.setCar(carBaseDto);
            rentalDto.setPayment(paymentDto);
            return rentalDto;
        }
        catch (ServiceUnavailableException e) {
            RentalDto rentalDto = gatewayMapper.toRentalDto(rentalResponse);
            rentalDto.setCar(carBaseDto);
            rentalDto.setPayment(new HashMap<>());
            return rentalDto;
        }
    }

    @Override
    public RentalCreationDto bookCar(String username, BookCarDto bookCarDto) {
        SagaContext context = new SagaContext();
        context.put("username", username);
        context.put("bookCarDto", bookCarDto);

        List<SagaStep<?>> steps = createBookCarSteps();
        try {
            RentalResponse rentalResponse = sagaOrchestrator.execute(steps, context);
            PaymentResponse paymentResponse = context.getResult("create-payment", PaymentResponse.class);
            RentalCreationDto result = gatewayMapper.toRentalCreationDto(rentalResponse);
            result.setPayment(gatewayMapper.toPaymentDto(paymentResponse));
            return result;
        } catch (SagaException e) {
            throw new ServiceUnavailableException(e.getMessage());
        }
    }

    private List<SagaStep<?>> createBookCarSteps() {
        return Arrays.asList(
                SagaStep.builder()
                        .name("check-and-reserve-car")
                        .critical(true)
                        .action(ctx -> {
                            BookCarDto dto = ctx.get("bookCarDto", BookCarDto.class);
                            CarResponse carResponse = carClient.getCar(dto.getCarUid()).getBody();
                            if (!carResponse.isAvailability()) {
                                throw new InvalidOperationException("Ошибка валидации данных");
                            }
                            carClient.changeAvailability(carResponse.getCarUid());
                            return carResponse;
                        })
                        .compensation(ctx -> {
                            CarResponse car = ctx.getResult("check-and-reserve-car", CarResponse.class);
                            if (car != null) {
                                carClient.changeAvailability(car.getCarUid());
                            }
                        })
                        .serviceName("Car")
                        .build(),
                SagaStep.builder()
                        .name("create-payment")
                        .critical(true)
                        .action(ctx -> {
                            BookCarDto dto = ctx.get("bookCarDto", BookCarDto.class);
                            CarResponse car = ctx.getResult("check-and-reserve-car", CarResponse.class);
                            return paymentClient.createPayment(Period.between(dto.getDateFrom(), dto.getDateTo()).getDays() * car.getPrice()).getBody();
                        })
                        .compensation(ctx -> {
                            PaymentResponse payment = ctx.getResult("create-payment", PaymentResponse.class);
                            if (payment != null) {
                                paymentClient.cancelPayment(payment.getPaymentUid());
                            }
                        })
                        .serviceName("Payment")
                        .build(),
                SagaStep.builder()
                        .name("create-rental")
                        .critical(true)
                        .action(ctx -> {
                            BookCarDto dto = ctx.get("bookCarDto", BookCarDto.class);
                            String user = ctx.get("username", String.class);
                            CarResponse car = ctx.getResult("check-and-reserve-car", CarResponse.class);
                            PaymentResponse payment = ctx.getResult("create-payment", PaymentResponse.class);

                            RentalRequest rentalRequest = RentalRequest.builder()
                                    .carUid(car.getCarUid())
                                    .paymentUid(payment.getPaymentUid())
                                    .dateFrom(dto.getDateFrom())
                                    .dateTo(dto.getDateTo())
                                    .build();

                            return rentalClient.createRental(user, rentalRequest).getBody();
                        })
                        .compensation(ctx -> {
                            RentalResponse rental = ctx.getResult("create-rental", RentalResponse.class);
                            String user = ctx.get("username", String.class);
                            if (rental != null) {
                                rentalClient.cancelRental(rental.getRentalUid(), user);
                            }
                        })
                        .serviceName("Payment")
                        .build()
        );
    }

    @Override
    public void finishRental(String username, UUID rentalUid) {
        RentalResponse rentalResponse = rentalClient.getRentalById(rentalUid, username).getBody();
        carClient.changeAvailability(rentalResponse.getCarUid());
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("rental-service");
        try {
            circuitBreaker.execute(() -> rentalClient.finishRental(rentalUid, username));
        }
        catch (CircuitBreakerException e) {
            sendFinishRentalToQueue(rentalUid, username);
        }
    }

    @Override
    public void cancelRental(String username, UUID rentalUid) {
        // что тут сделать = хрен пойми
        RentalResponse rentalResponse = rentalClient.getRentalById(rentalUid, username).getBody();
        carClient.changeAvailability(rentalResponse.getCarUid());
        CircuitBreaker circuitBreakerRental = circuitBreakerRegistry.getOrCreate("rental-service");
        try {
            circuitBreakerRental.execute(() -> rentalClient.cancelRental(rentalUid, username));
        }
        catch (CircuitBreakerException e) {
            sendCancelRentalToQueue(rentalUid, username, rentalResponse.getPaymentUid());
        }
        CircuitBreaker circuitBreakerPayment = circuitBreakerRegistry.getOrCreate("payment-service");
        try {
            circuitBreakerPayment.execute(() -> paymentClient.cancelPayment(rentalResponse.getPaymentUid()));
        }
        catch (CircuitBreakerException e) {
            sendCancelPaymentToQueue(rentalResponse.getPaymentUid(), rentalUid, username);
        }
    }

    private Page<CarDto> getAllCarsWithCircuitBreaker(Boolean showAll, Pageable pageable) {
        CircuitBreaker circuitBreakerCars = circuitBreakerRegistry.getOrCreate("cars-service");
        try {
            return circuitBreakerCars.execute(()->carClient.getCars(showAll, pageable).
                    getBody().map(gatewayMapper::toCarDto));
        } catch (CircuitBreakerException e) {
            throw new ServiceUnavailableException(ANSWER_FOR_SERVICE_UNAVAILABLE);
        }
    }

    // критично
    private List<RentalResponse> getRentalResponsesWithCircuitBreaker(String username) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("rentals-service");
        try {
            return circuitBreaker.execute(() ->
                    rentalClient.getAllRentals(username).getBody().stream().toList());
        } catch (CircuitBreakerException e) {
            throw new ServiceUnavailableException(ANSWER_FOR_SERVICE_UNAVAILABLE);
        }
    }

    // критично
    private RentalResponse getRentalByIdWithCircuitBreaker(UUID rentalUid, String username) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("rental-service");
        try {
            return circuitBreaker.execute(() ->
                    rentalClient.getRentalById(rentalUid, username).getBody());
        } catch (CircuitBreakerException e) {
            throw new ServiceUnavailableException(ANSWER_FOR_SERVICE_UNAVAILABLE);
        }
    }

    // не критично
    private PaymentDto getPaymentWithFallback(UUID paymentUid) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("payment-service");
        try {
            Optional<PaymentResponse> paymentResponse = circuitBreaker.execute(() ->
                    paymentClient.getPayment(paymentUid).getBody());
            return gatewayMapper.toPaymentDto(paymentResponse.get());
        }
        catch (CircuitBreakerException e) {
            throw new ServiceUnavailableException("");
        }
    }

    // не критично
    private CarBaseDto getCarWithFallback(UUID carUid) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.getOrCreate("cars-service");
        try {
            CarResponse carResponse = circuitBreaker.execute(() ->
                    carClient.getCar(carUid).getBody());
            return gatewayMapper.toCarBaseDto(carResponse);
        } catch (CircuitBreakerException e) {
            return createCarFallback(carUid);
        }
    }

    private CarBaseDto createCarFallback(UUID carUid) {
        CarBaseDto fallback = new CarBaseDto();
        fallback.setCarUid(carUid);
        return fallback;
    }

    private void sendFinishRentalToQueue(UUID rentalUid, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rentalUid", rentalUid.toString());
        payload.put("username", username);
        retryProducer.sendToRetryQueue(
                "FINISH_RENTAL",
                payload
        );
    }

    private void sendCancelRentalToQueue(UUID rentalUid, String username, UUID paymentUid) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("rentalUid", rentalUid.toString());
        payload.put("username", username);
        payload.put("paymentUid", paymentUid.toString());
        retryProducer.sendToRetryQueue(
                "CANCEL_RENTAL",
                payload
        );
    }

    private void sendCancelPaymentToQueue(UUID paymentUid, UUID rentalUid, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentUid", paymentUid.toString());
        payload.put("rentalUid", rentalUid.toString());
        payload.put("username", username);
        retryProducer.sendToRetryQueue(
                "CANCEL_PAYMENT",
                payload
        );
    }
}
