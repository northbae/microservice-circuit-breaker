package kz.bmstu.kritinina.client;

import kz.bmstu.kritinina.config.FeignConfig;
import kz.bmstu.kritinina.dto.CarResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@FeignClient(name = "car", url = "http://cars-service:8070/api/v1/cars", configuration = FeignConfig.class)
public interface CarClient {
    @GetMapping
    ResponseEntity<Page<CarResponse>> getCars(@RequestParam("showAll") Boolean showAll, @ParameterObject Pageable pageable);

    @PostMapping
    ResponseEntity<List<CarResponse>> getCars(@RequestParam("carUids") Set<UUID> carUids);

    @PostMapping(value = "/{carUid}")
    ResponseEntity<Void> changeAvailability(@PathVariable("carUid") UUID carUid);

    @GetMapping(value = "/{carUid}")
    ResponseEntity<CarResponse> getCar(@PathVariable("carUid") UUID carUid);
}
