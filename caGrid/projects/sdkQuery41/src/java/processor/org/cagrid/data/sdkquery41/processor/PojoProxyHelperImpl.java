package org.cagrid.data.sdkquery41.processor;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.proxy.ProxyHelperImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  PojoProxyHelperImpl
 *  Proxy helper implementation to simply return POJOs instead
 *  of Spring backed proxy domain objects
 * 
 * @author David Ervin
 * 
 * @created Jan 7, 2008 2:59:54 PM
 * @version $Id: PojoProxyHelperImpl.java,v 1.1 2008-12-22 20:02:55 dervin Exp $ 
 */
public class PojoProxyHelperImpl extends ProxyHelperImpl {
    
    private static Log LOG = LogFactory.getLog(PojoProxyHelperImpl.class);

    public Object convertToProxy(ApplicationService as, Object obj) {
        LOG.debug(obj.getClass().getName() + " was NOT proxied");
        return obj;
    }
}
