/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

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
