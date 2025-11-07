package kz.bmstu.kritinina.saga;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class SagaContext {
    private Map<String, Object> data = new HashMap<>();
    private Map<String, Object> results = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(data.get(key));
    }

    public void putResult(String stepName, Object result) {
        results.put(stepName, result);
    }

    public <T> T getResult(String stepName, Class<T> type) {
        return type.cast(results.get(stepName));
    }
}