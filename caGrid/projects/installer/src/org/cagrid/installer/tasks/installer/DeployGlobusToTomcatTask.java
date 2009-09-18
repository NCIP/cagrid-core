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
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class DeployGlobusToTomcatTask extends CaGridInstallerAntTask {

    /**
	 * 
	 */
    public DeployGlobusToTomcatTask(String name, String description) {
        super(name, description, null);
    }


    public String getBuildFilePath() {
        return new File("scripts/tomcat/build.xml").getAbsolutePath();
    }


    protected Object runAntTask(CaGridInstallerModel model, String buildFile, String target, Map<String, String> env,
        Properties sysProps) throws Exception {

        boolean secure = model.isTrue(Constants.USE_SECURE_CONTAINER);

        setStepCount(1);
        if (!secure) {
            new AntExecutionTask("", "", getBuildFilePath(), "globus-deploy-tomcat", env, sysProps).execute(model);
        } else {
            new AntExecutionTask("", "", getBuildFilePath(), "globus-deploy-secure-tomcat", env, sysProps)
                .execute(model);
        }

        return null;
    }

}
