/**
 * 
 */
package org.cagrid.installer.component;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.steps.RunTasksStep;
import org.cagrid.installer.tasks.ConditionalTask;
import org.cagrid.installer.tasks.installer.ConfigureAntTask;
import org.cagrid.installer.tasks.installer.ConfigureJBossTask;
import org.cagrid.installer.tasks.installer.DeployGlobusToJBossTask;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;

/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class AntComponentInstaller extends AbstractDownloadedComponentInstaller {

	/**
	 * 
	 */
	public AntComponentInstaller() {

	}

	protected String getComponentId() {
		return "ant";
	}

	protected Condition getShouldInstallCondition(){
		return new Condition() {
			public boolean evaluate(WizardModel m) {
				CaGridInstallerModel model = (CaGridInstallerModel) m;
				return  model.isTrue(Constants.INSTALL_CONFIGURE_CAGRID) && (!model.isAntInstalled()
						|| model.isTrue(Constants.REINSTALL_ANT));
			}
		};
	}

	protected Condition getShouldCheckReinstallCondition() {
		return new Condition() {
			public boolean evaluate(WizardModel m) {
				CaGridInstallerModel model = (CaGridInstallerModel) m;
				return model.isAntInstalled() && model.isTrue(Constants.INSTALL_CONFIGURE_CAGRID);
			}
		};
	}
	
    @Override
    public void addInstallDownloadedComponentTasks(CaGridInstallerModel model, RunTasksStep installAntTasks) {
        super.addInstallDownloadedComponentTasks(model, installAntTasks);

        installAntTasks.getTasks().add(
            new ConditionalTask(new ConfigureAntTask(model.getMessage("configuring.ant.title"), model
                .getMessage("configuring.ant.title")), getShouldInstallCondition()));

    }

}
