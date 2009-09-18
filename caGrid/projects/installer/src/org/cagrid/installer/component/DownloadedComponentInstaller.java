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
public interface DownloadedComponentInstaller {

	void addCheckInstallSteps(CaGridInstallerModel model);
	
	void addInstallDownloadedComponentTasks(CaGridInstallerModel model, RunTasksStep installStep);
}
