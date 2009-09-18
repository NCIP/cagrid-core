/**
 * 
 */
package org.cagrid.installer.tasks.installer;

import java.util.Map;
import java.util.Properties;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.tasks.AntExecutionTask;


public class DeployGlobusToJBossTask extends CaGridInstallerAntTask {

	/**
	 * 
	 */
	public DeployGlobusToJBossTask(String name, String description) {
		super(name, description, null);
	}

	protected Object runAntTask(CaGridInstallerModel model,String buildFile, String target, Map<String,String> env,
			Properties sysProps) throws Exception {

		boolean secure = model.isTrue(Constants.USE_SECURE_CONTAINER);
		sysProps.put("jboss.dir", model.getProperty(Constants.JBOSS_HOME));
		
		setStepCount(1);
		if (!secure) {
			new AntExecutionTask("", "", model.getProperty(Constants.CAGRID_HOME) + "/antfiles/jboss/jboss.xml", "deployJBoss", env, sysProps)
					.execute(model);
		} else {
			new AntExecutionTask("", "", model.getProperty(Constants.CAGRID_HOME) + "/antfiles/jboss/jboss.xml", "deploySecureJBoss", env, sysProps)
					.execute(model);
		}
		

		return null;
	}

}
