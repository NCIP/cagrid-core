package gov.nih.nci.cagrid.fqp.common;

import java.util.Map;

import org.apache.commons.collections.map.LRUMap;


/**
 * TimeLimitedCache
 * 
 * Caches a finite number of objects for a finite amount of time
 * When the maximum number of items is exceeded, the least recently used
 * item will be removed from the cache.  Items in the cache older than
 * the maximum age will not be returned, but discarded when getItem() is called
 * 
 * @author David
 *
 * @param <K> The key type
 * @param <T> The cached value type
 */
public class TimeLimitedCache<K, T> {

    public static final int DEFAULT_MAX_SIZE = 50;

    private Map<K, CachedValue<T>> cache = null;
    private long maxAge;


    public TimeLimitedCache(long maxAge) {
        this(maxAge, DEFAULT_MAX_SIZE);
    }


    @SuppressWarnings("unchecked")
    public TimeLimitedCache(long maxAge, int maxItems) {
        this.maxAge = maxAge;
        this.cache = new LRUMap(maxItems);
    }


    public synchronized void cacheItem(K key, T value) {
        cache.put(key, new CachedValue<T>(value));
    }


    public synchronized T getItem(K key) {
        T value = null;
        CachedValue<T> cached = cache.get(key);
        if (cached != null) {
            if (cached.getAge() <= maxAge) {
                // found, and not age exceeded
                value = cached.getValue();
            } else {
                // found, but too old
                cache.remove(key);
            }
        }
        return value;
    }


    private class CachedValue<V> {
        private V value;
        private long cacheTime;


        public CachedValue(V value) {
            this.value = value;
            this.cacheTime = System.currentTimeMillis();
        }


        public V getValue() {
            return value;
        }


        public long getAge() {
            return System.currentTimeMillis() - cacheTime;
        }
    }
}
