package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ContainerLogConfigUtil;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * UnpackContainerStep
 * Sets up a new service container environment
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 8, 2006
 * @version $Id: UnpackContainerStep.java,v 1.2 2009-01-21 22:05:20 dervin Exp $
 */
public class UnpackContainerStep extends Step {
    
    // A system property which may be set to a comma separated list of
    // Java packages which will be enabled for debugging in the container's
    // Log4j.properties file
    public static final String DEBUG_PACKAGES_PROPERTY = "testing.container.debug.packages";
    public static final String DEBUG_SOAP_PROPERTY = "testing.container.debug.soap";
    
    private static final Log LOG = LogFactory.getLog(UnpackContainerStep.class);
    
	private ServiceContainer container;

	public UnpackContainerStep(ServiceContainer container) {
		this.container = container;
	}

    
	public void runStep() throws Throwable {
		LOG.debug("Unpacking service container");
		container.unpackContainer();
        ContainerLogConfigUtil logConfigUtil = new ContainerLogConfigUtil(container);
        List<String> debugPackages = getDebugPackages();
        for (String pack : debugPackages) {
            LOG.debug("Adding package " + pack + " for debugging");
            logConfigUtil.setPackageDebug(pack, true);
        }
        boolean enableSoap = enableSoapDebug();
        LOG.debug("Setting SOAP logging " + (enableSoap ? "ENABLED" : "DISABLED"));
        logConfigUtil.setSoapLoggingEnabled(enableSoap);
	}
    
    
    public List<String> getDebugPackages() {
        List<String> packs = new LinkedList<String>();
        String value = System.getProperty(DEBUG_PACKAGES_PROPERTY);
        if (value != null) {
            String[] values = value.split(",");
            for (String pack : values) {
                packs.add(pack.trim());
            }
        } else {
            LOG.info("System property " + DEBUG_PACKAGES_PROPERTY + 
                " not defined; not configuring any packages for debugging");
        }
        return packs;
    }
    
    
    public boolean enableSoapDebug() {
        boolean debug = false;
        String value = System.getProperty(DEBUG_SOAP_PROPERTY);
        if (value != null) {
            try {
                debug = Boolean.parseBoolean(value);
            } catch (Exception ex) {
                LOG.warn("Could not parse value " + value + 
                    " of system property " + DEBUG_SOAP_PROPERTY, ex);
            }
        }
        return debug;
    }
}
