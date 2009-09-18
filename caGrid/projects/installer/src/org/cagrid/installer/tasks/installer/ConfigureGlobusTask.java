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
public class ConfigureGlobusTask extends CaGridInstallerAntTask {

    /**
     * @param name
     * @param description
     * @param targetName
     */
    public ConfigureGlobusTask(String name, String description) {
        super(name, description, null);
    }


    public String getBuildFilePath() {
        return new File("scripts/build.xml").getAbsolutePath();
    }


    protected Object runAntTask(CaGridInstallerModel model, String buildFile, String target, Map<String, String> env,
        Properties sysProps) throws Exception {

        setStepCount(1);
        new AntExecutionTask("", "", getBuildFilePath(), "fix-globus-permissions", env, sysProps).execute(model);
        setLastStep(1);

        return null;
    }

}
