package k_paas.balloon.keeper.infrastructure.persistence.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;

@Repository
public abstract class InMemoryStore {
    protected final ConcurrentMap<String, Object> store = new ConcurrentHashMap<>();

    public abstract void put(String key, Object value);
    public abstract Object get(String key);
    public abstract void remove(String key);
}
