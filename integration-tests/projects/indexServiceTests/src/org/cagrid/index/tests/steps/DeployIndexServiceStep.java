package org.cagrid.index.tests.steps;

import gov.nih.nci.cagrid.common.StreamGobbler;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerException;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatSecureServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeployIndexServiceStep extends Step {
    
    private static final Log LOG = LogFactory.getLog(DeployIndexServiceStep.class);
    
    public static final String DEPLOY_ANT_TARGET = "deployIndexTomcat";
    
    private ServiceContainer container;
    private File indexServiceDir;
    
    public DeployIndexServiceStep(ServiceContainer container, File indexServiceDir) {
        this.container = container;
        this.indexServiceDir = indexServiceDir;
    }
    

    public void runStep() throws Throwable {
        // assertTrue("Testing Index service can only be deployed to secure tomcat", container instanceof TomcatSecureServiceContainer);
        String antHome = System.getenv(TomcatSecureServiceContainer.ENV_ANT_HOME);
        if (antHome == null || antHome.equals("")) {
            throw new ContainerException(TomcatSecureServiceContainer.ENV_ANT_HOME + " not set");
        }
        File ant = new File(antHome, "bin" + File.separator + "ant");

        List<String> command = new ArrayList<String>();

        // executable to call
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command.add("cmd");
            command.add("/c");
            command.add(ant.getAbsolutePath() + ".bat");
        } else {
            command.add(ant.getAbsolutePath());
        }

        // target to execute
        command.add(DEPLOY_ANT_TARGET);

        // environment variables
        List<String> additionalEnvironment = new ArrayList<String>();
        
        // set catalina home
        additionalEnvironment.add(TomcatSecureServiceContainer.ENV_CATALINA_HOME + "="
            + container.getProperties().getContainerDirectory().getAbsolutePath());
        String[] editedEnvironment = editEnvironment(additionalEnvironment);        

        LOG.debug("Command environment:\n");
        for (String e : editedEnvironment) {
            LOG.debug(e);
        }

        String[] commandArray = command.toArray(new String[command.size()]);
        Process deployProcess = null;
        try {
            deployProcess = Runtime.getRuntime().exec(commandArray,
                    editedEnvironment, indexServiceDir);
            new StreamGobbler(deployProcess.getInputStream(),
                    StreamGobbler.TYPE_OUT, System.out).start();
            new StreamGobbler(deployProcess.getErrorStream(),
                    StreamGobbler.TYPE_OUT, System.err).start();
            deployProcess.waitFor();
        } catch (Exception ex) {
            throw new ContainerException("Error invoking deploy process: "
                    + ex.getMessage(), ex);
        }

        if (deployProcess.exitValue() != 0) {
            throw new ContainerException("deployService ant command failed: "
                    + deployProcess.exitValue());
        }
    }
    
    
    private String[] editEnvironment(List<String> edits) {
        Map<String, String> envm = new HashMap<String, String>(System.getenv());
        for (String element : edits) {
            String[] envVar = element.split("=");
            envm.put(envVar[0], envVar[1]);
        }
        String[] environment = new String[envm.size()];
        Iterator<String> keys = envm.keySet().iterator();
        int i = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            environment[i++] = key + "=" + envm.get(key);
        }
        return environment;
    }
}
