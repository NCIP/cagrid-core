/**
 * 
 */
package org.cagrid.installer.component;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.RunTasksStep;

/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public interface CaGridComponentInstaller {

	void addSteps(CaGridInstallerModel model);
	void addInstallTasks(CaGridInstallerModel model, RunTasksStep installStep);
	
}
