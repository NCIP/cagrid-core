/**
 * 
 */
package org.cagrid.installer.tasks.installer;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.tasks.AntExecutionTask;


/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class ConfigureTomcatTask extends CaGridInstallerAntTask {

    /**
     * @param name
     * @param description
     * @param targetName
     */
    public ConfigureTomcatTask(String name, String description) {
        super(name, description, null);
    }


    public ConfigureTomcatTask(String name, String description,  String targetName) {
        super(name, description, targetName);
    }
    

    protected Object runAntTask(CaGridInstallerModel model, String buildFile, String target, Map<String,String> env,
            Properties sysProps) throws Exception {

        boolean secure = model.isTrue(Constants.USE_SECURE_CONTAINER);

        if (!secure) {
            setStepCount(5);
            new AntExecutionTask("", "", getBuildFilePath(), "fix-web-xml", env, sysProps).execute(model);
            setLastStep(1);
            new AntExecutionTask("", "", getBuildFilePath(), "configure-tomcat-ports", env, sysProps).execute(model);
            setLastStep(2);
            new AntExecutionTask("", "", getBuildFilePath(), "configure-tomcat-shutdown-ports", env, sysProps).execute(model);
            setLastStep(3);
            new AntExecutionTask("", "", getBuildFilePath(), "configure-server-config", env, sysProps)
            .execute(model);
            setLastStep(4);
        } else {
            setStepCount(7);
            new AntExecutionTask("", "", getBuildFilePath(), "insert-secure-connector", env, sysProps)
                    .execute(model);
            setLastStep(1);
            new AntExecutionTask("", "", getBuildFilePath(), "insert-valve", env, sysProps).execute(model);
            setLastStep(2);
            new AntExecutionTask("", "", getBuildFilePath(), "set-global-cert-and-key-paths", env, sysProps)
                    .execute(model);
            setLastStep(3);
            new AntExecutionTask("", "", getBuildFilePath(), "fix-secure-web-xml", env, sysProps)
                    .execute(model);
            setLastStep(4);
            new AntExecutionTask("", "", getBuildFilePath(), "configure-tomcat-shutdown-ports", env, sysProps).execute(model);
            setLastStep(5);
            new AntExecutionTask("", "", getBuildFilePath(), "configure-server-config", env, sysProps)
                    .execute(model);
            setLastStep(6);
        }
        
        new AntExecutionTask("", "", getBuildFilePath(), "fix-permissions", env, sysProps)
        .execute(model);
        
        return null;
    }


    public String getBuildFilePath(){
        return new File("scripts/tomcat/build.xml").getAbsolutePath();
    }


}
