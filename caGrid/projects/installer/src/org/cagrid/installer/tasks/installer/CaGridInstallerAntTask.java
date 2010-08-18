/**
 * 
 */
package org.cagrid.installer.tasks.installer;

import java.util.Map;
import java.util.Properties;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.tasks.AntExecutionTask;
import org.cagrid.installer.tasks.BasicTask;
import org.cagrid.installer.util.InstallerUtils;


/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class CaGridInstallerAntTask extends BasicTask {

    private String targetName;


    /**
     * @param name
     * @param description
     * @param targetName
     */
    public CaGridInstallerAntTask(String name, String description, String targetName) {
        super(name, description);
        this.targetName = targetName;
    }


    /*
     * (non-Javadoc)
     * @see org.cagrid.installer.tasks.BasicTask#internalExecute(java.util.Map)
     */
    @Override
    protected Object internalExecute(CaGridInstallerModel model) throws Exception {
        Map<String, String> env = InstallerUtils.getEnvironment(model);
        env.put("GLOBUS_LOCATION", model.getProperty(Constants.GLOBUS_HOME));
        env.put("CATALINA_HOME", model.getProperty(Constants.TOMCAT_HOME));
        env.put("JBOSS_HOME", model.getProperty(Constants.JBOSS_HOME));
        
        Properties sysProps = InstallerUtils.getProxyProperties();
        if (model.getProperty(Constants.GLOBUS_HOME) != null) {
            sysProps.setProperty("env.GLOBUS_LOCATION", model.getProperty(Constants.GLOBUS_HOME));
        }
        return runAntTask(model, "scripts/build.xml", this.targetName, env, sysProps);

    }


    protected Object runAntTask(CaGridInstallerModel model, String buildFile, String target, Map<String, String> env,
        Properties sysProps) throws Exception {

        return new AntExecutionTask("", "", buildFile, target, env, sysProps).execute(model);

    }
}
