package gov.nih.nci.cagrid.gts.service.globus.resource;

import gov.nih.nci.cagrid.gts.common.GTSConstants;
import gov.nih.nci.cagrid.gts.stubs.GTSResourceProperties;
import gov.nih.nci.cagrid.introduce.servicetools.SingletonResourceHomeImpl;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.InvalidResourceKeyException;
import org.globus.wsrf.PersistenceCallback;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.jndi.Initializable;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements the resource home for the resource type represented
 * by this service.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class GTSResourceHome extends SingletonResourceHomeImpl implements Initializable {

	static final Log logger = LogFactory.getLog(GTSResourceHome.class);
    private static final UUIDGen UUIDGEN = UUIDGenFactory.getUUIDGen();

	public Resource createSingleton() {
		logger.info("Creating a single resource.");
		try {
		    GTSResourceProperties props = new GTSResourceProperties();
			GTSResource resource = new GTSResource();
			if (resource instanceof PersistenceCallback) {
			      //try to load the resource if it was persisted
                  try{
                    ((PersistenceCallback) resource).load(null);
			      } catch (InvalidResourceKeyException ex){
			      	  //persisted singleton resource was not found so we will just create a new one
			          resource.initialize(props, GTSConstants.RESOURCE_PROPERTY_SET, UUIDGEN.nextUUID());
			      }
            } else {
                    resource.initialize(props, GTSConstants.RESOURCE_PROPERTY_SET, UUIDGEN.nextUUID());
            }
			
			return resource;
		} catch (Exception e) {
			logger.error("Exception when creating the resource",e);
			return null;
		}
	}


	public Resource find(ResourceKey key) throws ResourceException {
		GTSResource resource = (GTSResource) super.find(key);
		return resource;
	}


	/**
	 * Initialze the singleton resource, when the home is initialized.
	 */
	public void initialize() throws Exception {
		logger.info("Attempting to initialize resource.");
		Resource resource = find(null);
		if (resource == null) {
			logger.error("Unable to initialize resource!");
		} else {
			logger.info("Successfully initialized resource.");
		}
	}
	
    /**
     * Get the resouce that is being addressed in this current context
     */
    public GTSResource getAddressedResource() throws Exception {
        GTSResource thisResource;
        thisResource = (GTSResource) ResourceContext.getResourceContext().getResource();
        return thisResource;
    }
}