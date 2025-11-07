package kz.bmstu.kritinina.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Stack;

@Slf4j
@Component
public class SagaOrchestrator {

    public <T> T execute(List<SagaStep<?>> steps, SagaContext context) throws SagaException {
        Stack<SagaStep<?>> executedSteps = new Stack<>();

        try {
            for (SagaStep<?> step : steps) {
                try {
                    Object result = step.getAction().apply(context);
                    context.putResult(step.getName(), result);
                    executedSteps.push(step);

                    log.info("Saga step completed: {}", step.getName());

                } catch (Exception e) {
                    log.error("Saga step failed: {}, error: {}", step.getName(), e.getMessage());

                    if (step.isCritical()) {
                        log.error("Critical step failed, starting rollback");
                        rollback(executedSteps, context);
                        throw new SagaException("Saga failed at step: " + step.getName());
                    } else {
                        log.warn("Non-critical step failed, continuing saga");
                    }
                }
            }

            log.info("Saga completed successfully");
            return (T) context.getResults().get(steps.get(steps.size() - 1).getName());

        } catch (Exception e) {
            if (!(e instanceof SagaException)) {
                rollback(executedSteps, context);
            }
            throw e;
        }
    }

    private void rollback(Stack<SagaStep<?>> executedSteps, SagaContext context) {
        log.warn("Starting saga rollback, steps to compensate: {}", executedSteps.size());

        while (!executedSteps.isEmpty()) {
            SagaStep<?> step = executedSteps.pop();

            if (step.getCompensation() != null) {
                try {
                    log.info("Compensating step: {}", step.getName());
                    step.getCompensation().accept(context);
                    log.info("Compensation completed for: {}", step.getName());
                } catch (Exception e) {
                    log.error("Compensation failed for step: {}, error: {}",
                            step.getName(), e.getMessage(), e);
                }
            }
        }

        log.info("Saga rollback completed");
    }
}