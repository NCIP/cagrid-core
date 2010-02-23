package gov.nih.nci.cagrid.fqp;

import java.util.HashMap;
import java.util.Map;

import gov.nih.nci.cagrid.fqp.common.TimeLimitedCache;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TimeLimitedCacheTestCase extends TestCase {
    
    public TimeLimitedCacheTestCase(String name) {
        super(name);
    }
    
    
    public void testCacheSizeLimit() {
        // create a cache with max of 3 items
        TimeLimitedCache<String, String> cache = new TimeLimitedCache<String, String>(Long.MAX_VALUE, 3);
        // map to hang on to everything
        Map<String, String> map = new HashMap<String, String>();
        
        // put four items in the cache and map, verify only three are available in the map
        for (int i = 0; i < 4; i++) {
            String key = "key" + i;
            String val = "value" + i;
            cache.cacheItem(key, val);
            map.put(key, val);
        }
        
        // go through the keys again, verifying the returned object is null for 0,
        // and == what's in the map for the rest
        for (int i = 0; i < 4; i++) {
            String key = "key" + i;
            String val = map.get(key);
            assertNotNull("Map should have contained a value for " + key, val);
            String cached = cache.getItem(key);
            if (i == 0) {
                assertNull("Cache should have removed item", cached);
            } else {
                assertNotNull("Cache removed item for key " + key + " but should not have");
                assertEquals("Cached item was not as expected", val, cached);
            }
        }
    }
    
    
    public void testCacheTimeout() {
        // create a cache with a 1 second timeout
        TimeLimitedCache<String, String> cache = new TimeLimitedCache<String, String>(1000);
        // put four items in the cache
        for (int i = 0; i < 4; i++) {
            String key = "key" + i;
            String val = "value" + i;
            cache.cacheItem(key, val);
        }
        // wait 2000 ms for the items in the cache to age
        sleep(2000);
        // verify everything in the cache is gone
        for (int i = 0; i < 4; i++) {
            String key = "key" + i;
            String val = cache.getItem(key);
            assertNull("Cache should have removed old item, but did not for key " + key, val);
        }
    }
    
    
    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            // meh
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(TimeLimitedCacheTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
