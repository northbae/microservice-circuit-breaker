package kz.bmstu.kritinina.saga;

import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;
import java.util.function.Function;

@Data
@Builder
public class SagaStep<T> {
    private String name;
    private Function<SagaContext, T> action;
    private Consumer<SagaContext> compensation;
    private boolean critical;
    private String serviceName;
}