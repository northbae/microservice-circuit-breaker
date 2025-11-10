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

                } catch (Exception e) {
                    if (step.isCritical()) {
                        rollback(executedSteps, context);
                        throw new SagaException(step.getServiceName() + " Service unavailable");
                    }
                }
            }
            return (T) context.getResults().get(steps.get(steps.size() - 1).getName());
        } catch (Exception e) {
            if (!(e instanceof SagaException)) {
                rollback(executedSteps, context);
            }
            throw e;
        }
    }

    private void rollback(Stack<SagaStep<?>> executedSteps, SagaContext context) {
        while (!executedSteps.isEmpty()) {
            SagaStep<?> step = executedSteps.pop();
            if (step.getCompensation() != null) {
                try {
                    step.getCompensation().accept(context);
                } catch (Exception e) {
                    throw new SagaException("Compensation method isnt work");
                }
            }
        }
    }
}