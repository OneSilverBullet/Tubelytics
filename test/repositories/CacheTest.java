package repositories;

import org.junit.Test;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the Cache
 *
 * @author Feng Zhao
 */
public class CacheTest {
    /**
     * Tests an empty cache
     *
     * @author Feng Zhao
     */
    @Test
    public void testCacheEmpty() {
        Cache<Integer> cache = new Cache<>();
        assertTrue(cache.get("Empty").isEmpty());
    }

    /**
     * Tests a cache get
     *
     * @author Feng Zhao
     */
    @Test
    public void testCacheGet() {
        Cache<Integer> cache = new Cache<>();
        cache.put("Test", 1);
        assertEquals(Optional.of(1), cache.get("Test"));
    }

    /**
     * Tests a cache miss
     *
     * @author Feng Zhao
     */
    @Test
    public void testCacheMiss() {
        Cache<Integer> cache = new Cache<>();
        cache.put("Test", 1);
        assertEquals(Optional.empty(), cache.get("Different"));
    }

    /**
     * Tests a cache miss with an old value
     *
     * @author Feng Zhao
     */
    @Test
    public void testCacheMissOld() {
        Cache<Integer> cache = new Cache<>();
        cache.put("Test", LocalTime.now().minusMinutes(20L), 1);
        assertEquals(Optional.empty(), cache.get("Test"));
    }
}
