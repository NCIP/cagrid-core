/**
 * 
 */
package org.cagrid.installer.component;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.CheckReInstallStep;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.steps.InstallInfoStep;
import org.cagrid.installer.steps.RunTasksStep;
import org.cagrid.installer.tasks.ConditionalTask;
import org.cagrid.installer.tasks.DownloadFileTask;
import org.cagrid.installer.tasks.UnzipInstallTask;
import org.pietschy.wizard.models.Condition;


/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public abstract class AbstractDownloadedComponentInstaller implements DownloadedComponentInstaller {

    /**
	 * 
	 */
    public AbstractDownloadedComponentInstaller() {

    }


    /*
     * (non-Javadoc)
     * @see
     * org.cagrid.installer.ExternalComponentInstaller#addCheckInstallSteps(
     * org.cagrid.installer.model.CaGridInstallerModel)
     */
    public void addCheckInstallSteps(CaGridInstallerModel model) {
        CheckReInstallStep checkInstallStep = new CheckReInstallStep(model.getMessage(getComponentId()
            + ".check.reinstall.title"), model.getMessage(getComponentId() + ".check.reinstall.desc"), getComponentId()
            + ".home", "reinstall." + getComponentId());
        model.add(checkInstallStep, getShouldCheckReinstallCondition());

        InstallInfoStep installInfoStep = new InstallInfoStep(model.getMessage(getComponentId() + ".home.title"), model
            .getMessage(getComponentId() + ".home.desc"), getComponentId() + ".install.dir.path");
        model.add(installInfoStep, getShouldInstallCondition());
    }


    /*
     * (non-Javadoc)
     * @seeorg.cagrid.installer.ExternalComponentInstaller#
     * addInstallExternalComponentTasks
     * (org.cagrid.installer.model.CaGridInstallerModel,
     * org.cagrid.installer.steps.RunTasksStep)
     */
    public void addInstallDownloadedComponentTasks(CaGridInstallerModel model, RunTasksStep deployContainer) {

        deployContainer.getTasks().add(
            new ConditionalTask(new DownloadFileTask(model.getMessage("downloading." + getComponentId() + ".title"),
                model.getMessage("downloading." + getComponentId() + ".title"), getComponentId() + ".download.url", getComponentId() + ".temp.file.name", getComponentId()
                    + ".md5.checksum", Constants.CONNECT_TIMEOUT),

            getShouldInstallCondition()));

        deployContainer.getTasks().add(
            new ConditionalTask(new UnzipInstallTask(model.getMessage("installing." + getComponentId() + ".title"), model.getMessage("installing." + getComponentId() + ".title"),
                getComponentId() + ".temp.file.name", getComponentId() + ".install.dir.path", getComponentId()
                    + ".dir.name", getComponentId() + ".home"), getShouldInstallCondition()));

    }


    protected abstract String getComponentId();


    protected abstract Condition getShouldCheckReinstallCondition();


    protected abstract Condition getShouldInstallCondition();

}
