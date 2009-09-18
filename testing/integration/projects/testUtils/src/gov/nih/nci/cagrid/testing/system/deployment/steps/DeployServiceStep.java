package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DeployServiceStep 
 * Deploys a caGrid service to the container
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 8, 2006
 * @version $Id: DeployServiceStep.java,v 1.1 2008-05-14 17:17:42 hastings Exp $
 */
public class DeployServiceStep extends Step {
    private static final Log LOG = LogFactory.getLog(DeployServiceStep.class);

	private ServiceContainer container;
	private String serviceBase;
    private List<String> deployArgs;

	public DeployServiceStep(ServiceContainer container, String serviceBaseDir) {
		this(container, serviceBaseDir, null);
	}
    
    
    public DeployServiceStep(ServiceContainer container, String serviceBaseDir, List<String> args) {
        this.container = container;
        this.serviceBase = serviceBaseDir;
        this.deployArgs = args;
    }

	
    public void runStep() throws Throwable {
		LOG.debug("Running step: " + getClass().getName());
		File serviceBaseDir = new File(serviceBase);
        if (deployArgs == null) {
            container.deployService(serviceBaseDir);
        } else {
            container.deployService(serviceBaseDir, deployArgs);
        }
	}
}
