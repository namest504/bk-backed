package k_paas.balloon.keeper.infrastructure.persistence.memory;

import org.springframework.stereotype.Repository;

@Repository
public class ClimateDataInMemoryStore extends InMemoryStore {

    public static final String RECENT_PATH = "resentPath";

    @Override
    public void put(String key, Object value) {
        store.put(key, String.valueOf(value));
    }

    @Override
    public String get(String key) {
        return (String) store.get(key);
    }

    @Override
    public void remove(String key) {
        store.remove(key);
    }
}
