package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * StopContainerStep 
 * Shuts down a service container
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 8, 2006
 * @version $Id: StopContainerStep.java,v 1.1 2008-05-14 17:17:42 hastings Exp $
 */
public class StopContainerStep extends Step {
    private static final Log LOG = LogFactory.getLog(StopContainerStep.class);

	private ServiceContainer container;

	public StopContainerStep(ServiceContainer container) {
		this.container = container;
	}

    
	public void runStep() throws Throwable {
		LOG.debug("Stopping temporary service container");

		container.stopContainer();
	}
}
