package repositories;

import java.time.LocalTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for holding frequently requested objects
 *
 * @param <T> Type of object to cache
 * @author Wayan-Gwie Lapointe and Feng Zhao
 */
public class Cache<T> {
    private static final Long CACHE_ACTIVATE_TIME_SLOT = 3L;

    private final ConcurrentHashMap<String, CacheResult<T>> cache = new ConcurrentHashMap<>();

    /**
     * Get from the cache
     * Only returns the objects if it's less than 3 minutes old
     *
     * @param key Key for the object
     * @return Optional containing the object if one exists
     * @author Wayan-Gwie Lapointe and Feng Zhao
     */
    public Optional<T> get(String key) {
        if (cache.containsKey(key) && cache.get(key).getInsertionTime().isAfter(LocalTime.now().minusMinutes(CACHE_ACTIVATE_TIME_SLOT))) {
            return Optional.of(cache.get(key).getObj());
        }

        return Optional.empty();
    }

    /**
     * Put into the cache
     *
     * @param key    Key for the object
     * @param object Object to store in the cache
     * @author Wayan-Gwie Lapointe and Feng Zhao
     */
    public void put(String key, T object) {
        cache.put(key, new CacheResult<>(object));
    }

    /**
     * Put into the cache at a specific time
     *
     * @param key    Key for the object
     * @param time   Time of insertion
     * @param object Object to store in the cache
     * @author Wayan-Gwie Lapointe and Feng Zhao
     */
    public void put(String key, LocalTime time, T object) {
        cache.put(key, new CacheResult<>(time, object));
    }

    /**
     * Object container in the cache
     *
     * @param <T> Type of object in the cache
     * @author Wayan-Gwie Lapointe and Feng Zhao
     */
    private static class CacheResult<T> {
        private final LocalTime time;
        private final T obj;

        /**
         * Create a container for a cache object
         *
         * @param obj Object to cache
         * @author Wayan-Gwie Lapointe and Feng Zhao
         */
        public CacheResult(T obj) {
            this.time = LocalTime.now();
            this.obj = obj;
        }

        /**
         * Create a container for a cache object
         *
         * @param time Time of insertion
         * @param obj  Object to cache
         * @author Wayan-Gwie Lapointe and Feng Zhao
         */
        public CacheResult(LocalTime time, T obj) {
            this.time = time;
            this.obj = obj;
        }

        /**
         * Get the time of insertion into the cache
         *
         * @return Local time of insertion
         * @author Wayan-Gwie Lapointe and Feng Zhao
         */
        public LocalTime getInsertionTime() {
            return time;
        }

        /**
         * Get the object inserted into the cache
         *
         * @return Cached object
         * @author Wayan-Gwie Lapointe and Feng Zhao
         */
        public T getObj() {
            return obj;
        }
    }
}


