package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DeployServiceStep Deploys a caGrid service to the container
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 8, 2006
 * @version $Id: DeleteServiceStep.java,v 1.1 2008-05-19 16:39:23 langella Exp $
 */
public class DeleteServiceStep extends Step {
	private static final Log LOG = LogFactory.getLog(DeleteServiceStep.class);

	private File serviceDir;

	public DeleteServiceStep(File serviceDir) {
		this.serviceDir = serviceDir;
	}

	public void runStep() throws Throwable {
		LOG.debug("Running step: " + getClass().getName());
		Utils.deleteDir(serviceDir);
	}
}
