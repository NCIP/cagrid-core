/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * This step cleans up a stopped globus container by deleting it.
 * @author Patrick McConnell
 */
public class GlobusCleanupStep
	extends Step
{
	private GlobusHelper globus;
	
	public GlobusCleanupStep(GlobusHelper globus)
	{
		super();
		
		this.globus = globus;
	}
	
	public void runStep() throws Throwable
	{
		globus.cleanupTempGlobus();
	}
}