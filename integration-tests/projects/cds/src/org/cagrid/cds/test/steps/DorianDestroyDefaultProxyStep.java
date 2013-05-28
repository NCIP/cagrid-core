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
/*
 * Created on Jul 14, 2006
 */
package org.cagrid.cds.test.steps;

import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * This step destroys the globus default user proxy.
 * @author Patrick McConnell
 */
public class DorianDestroyDefaultProxyStep
	extends Step
{
	public DorianDestroyDefaultProxyStep() 
	{
		super();
	}
	
	public void runStep() 
		throws Throwable
	{
		ProxyUtil.destroyDefaultProxy();
	}
}
