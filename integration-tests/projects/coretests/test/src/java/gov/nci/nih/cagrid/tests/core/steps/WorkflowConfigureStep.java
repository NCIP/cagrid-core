/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.util.FileUtils;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/**
 * This step deploys a service to a temporary globus container by running the deployGlobus ant task
 * in the service directory.
 * @author Patrick McConnell
 */
public class WorkflowConfigureStep
	extends Step
{
	private GlobusHelper globus;
	private String activeBpelEndpoint;
	
	public WorkflowConfigureStep(GlobusHelper globus)
	{
		this(globus, System.getProperty("activebpel.endpoint", "http://localhost:7080/active-bpel/services"));
	}
	
	public WorkflowConfigureStep(GlobusHelper globus, String activeBpelEndpoint)
	{
		super();
		
		this.globus = globus;
		this.activeBpelEndpoint = activeBpelEndpoint;
	}
	
	public void runStep() throws Throwable
	{
		File jndiConfig = new File(globus.getTempGlobusLocation(), 
			"etc" + File.separator + "WorkflowManagementService" + File.separator + "jndi-config.xml"
		);
		FileUtils.replace(jndiConfig, 
			"http://spirulina.ci.uchicago.edu:8080/active-bpel/services/",
			activeBpelEndpoint
		);
	}
}