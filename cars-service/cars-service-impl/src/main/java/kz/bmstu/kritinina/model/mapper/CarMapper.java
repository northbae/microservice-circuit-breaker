package kz.bmstu.kritinina.model.mapper;

import kz.bmstu.kritinina.dto.CarResponse;
import kz.bmstu.kritinina.model.entity.Car;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CarMapper {
    CarResponse toCarResponse(Car car);
}
