/**
 * 
 */
package org.cagrid.installer.component;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.steps.RunTasksStep;
import org.cagrid.installer.tasks.ConditionalTask;
import org.cagrid.installer.tasks.installer.ConfigureAntTask;
import org.cagrid.installer.tasks.installer.ConfigureGlobusTask;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;

/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 *
 */
public class GlobusComponentInstaller extends
		AbstractDownloadedComponentInstaller {

	/**
	 * 
	 */
	public GlobusComponentInstaller() {
	}

	/* (non-Javadoc)
	 * @see org.cagrid.installer.AbstractDownloadedComponentInstaller#getComponentId()
	 */
	@Override
	protected String getComponentId() {
		return "globus";
	}

	/* (non-Javadoc)
	 * @see org.cagrid.installer.AbstractDownloadedComponentInstaller#getShouldCheckCondition()
	 */
	@Override
	protected Condition getShouldCheckReinstallCondition() {
		return new Condition() {
			public boolean evaluate(WizardModel m) {
				CaGridInstallerModel model = (CaGridInstallerModel) m;
				return model.isGlobusInstalled() && model.isTrue(Constants.INSTALL_CONFIGURE_CAGRID);
			}
		};
	}
	
	protected Condition getShouldInstallCondition(){
		return new Condition() {
			public boolean evaluate(WizardModel m) {
				CaGridInstallerModel model = (CaGridInstallerModel) m;
				return model.isTrue(Constants.INSTALL_CONFIGURE_CAGRID) && (!model.isGlobusInstalled()
						|| model.isTrue(Constants.REINSTALL_GLOBUS));
			}
		};
	}
	
	
    @Override
    public void addInstallDownloadedComponentTasks(CaGridInstallerModel model, RunTasksStep installAntTasks) {
        super.addInstallDownloadedComponentTasks(model, installAntTasks);

        installAntTasks.getTasks().add(
            new ConditionalTask(new ConfigureGlobusTask(model.getMessage("configuring.globus.title"), model
                .getMessage("configuring.globus.title")), getShouldInstallCondition()));

    }


}
