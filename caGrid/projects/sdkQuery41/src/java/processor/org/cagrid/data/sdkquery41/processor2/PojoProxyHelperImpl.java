/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.data.sdkquery41.processor2;

import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.proxy.ProxyHelperImpl;

import org.apache.log4j.Logger;

/** 
 *  PojoProxyHelperImpl
 *  Proxy helper implementation to simply return POJOs instead
 *  of Spring backed proxy domain objects
 * 
 * @author David Ervin
 * 
 * @created Jan 7, 2008 2:59:54 PM
 * @version $Id: PojoProxyHelperImpl.java,v 1.1 2008/04/02 14:46:19 dervin Exp $ 
 */
public class PojoProxyHelperImpl extends ProxyHelperImpl {
    
    private static Logger LOG = Logger.getLogger(PojoProxyHelperImpl.class);

    public Object convertToProxy(ApplicationService as, Object obj) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Instance of " + obj.getClass().getName() + " was NOT proxied");
        }
        return obj;
    }
}
