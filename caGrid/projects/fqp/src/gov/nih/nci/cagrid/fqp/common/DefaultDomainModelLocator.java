package gov.nih.nci.cagrid.fqp.common;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

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
    public static final long DEFAULT_CACHE_TIME = 20 * 60 * 1000; // 20 minutes
    public static final int MAX_CACHED_MODELS = 25;
    
    private static Log LOG = LogFactory.getLog(DefaultDomainModelLocator.class);
    
    private TimeLimitedCache<String, DomainModel> modelCache = null;
    
    public DefaultDomainModelLocator() {
        this(DEFAULT_CACHE_TIME);
    }
    
    
    public DefaultDomainModelLocator(long cacheTimeMills) {
        this.modelCache = new TimeLimitedCache<String, DomainModel>(cacheTimeMills, MAX_CACHED_MODELS);
    }
    

    public DomainModel getDomainModel(String targetServiceUrl) throws Exception {
        LOG.debug("Getting domain model for " + targetServiceUrl);
        DomainModel model = modelCache.getItem(targetServiceUrl);
        if (model != null) {
            LOG.debug("Model was in the cache");
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
        modelCache.cacheItem(targetServiceUrl, model);
        return model;
    }
}
