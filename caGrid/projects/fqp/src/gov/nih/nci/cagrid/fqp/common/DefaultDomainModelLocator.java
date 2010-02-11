package gov.nih.nci.cagrid.fqp.common;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.util.HashMap;
import java.util.Map;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DefaultDomainModelLocator
 * Default implementation of the domain model locator looks up
 * domain models from the requested service URL and caches it
 * for a configurable amount of time (20 minutes by default)
 * 
 * @author David
 */
public class DefaultDomainModelLocator implements DomainModelLocator {
    
    private static Log LOG = LogFactory.getLog(DefaultDomainModelLocator.class);
    private static long DEFAULT_CACHE_TIME = 20 * 60 * 1000; // 20 minutes
    
    private long cacheTimeMills;
    private Map<String, CachedDomainModel> cachedModels = null;
    
    public DefaultDomainModelLocator() {
        this(DEFAULT_CACHE_TIME);
    }
    
    
    public DefaultDomainModelLocator(long cacheTimeMills) {
        this.cacheTimeMills = cacheTimeMills;
        this.cachedModels = new HashMap<String, CachedDomainModel>();
    }
    

    public synchronized DomainModel getDomainModel(String targetServiceUrl) throws Exception {
        LOG.debug("Getting domain model for " + targetServiceUrl);
        CachedDomainModel cachedModel = cachedModels.get(targetServiceUrl);
        DomainModel model = null;
        if (cachedModel != null) {
            LOG.debug("Model was in the cache");
            long age = System.currentTimeMillis() - cachedModel.cacheTime;
            if (age > cacheTimeMills) {
                LOG.debug("Cached model is too old");
                cachedModels.remove(targetServiceUrl);
                model = getAndCacheModel(targetServiceUrl);
            } else {
                LOG.debug("Model age does not excede " + cacheTimeMills + " ms");
                model = cachedModel.model;
            }
        } else {
            LOG.debug("Model was not in the cache");
            model = getAndCacheModel(targetServiceUrl);
        }
        return model;
    }
    
    
    private DomainModel getAndCacheModel(String targetServiceUrl) throws Exception {
        LOG.debug("Retrieving domain model from " + targetServiceUrl);
        DomainModel model = MetadataUtils.getDomainModel(
            new EndpointReferenceType(new Address(targetServiceUrl)));
        CachedDomainModel cached = new CachedDomainModel();
        cached.cacheTime = System.currentTimeMillis();
        cached.model = model;
        cachedModels.put(targetServiceUrl, cached);
        return model;
    }
    
    
    private static class CachedDomainModel {
        public long cacheTime;
        public DomainModel model;
    }
}
