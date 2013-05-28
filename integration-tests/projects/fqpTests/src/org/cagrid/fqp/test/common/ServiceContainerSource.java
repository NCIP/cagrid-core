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
