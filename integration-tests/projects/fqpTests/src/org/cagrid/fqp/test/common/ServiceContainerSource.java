package org.cagrid.fqp.test.common;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

/** 
 *  DataServiceContainerSource
 *  Interface hides the actual means of getting a ServiceContainer
 * 
 * @author David Ervin
 * 
 * @created Jul 10, 2008 11:01:38 AM
 * @version $Id: ServiceContainerSource.java,v 1.1 2008-07-15 19:44:13 dervin Exp $ 
 */
public interface ServiceContainerSource {

    public ServiceContainer getServiceContainer();
}
