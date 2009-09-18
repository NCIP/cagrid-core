/**
 * 
 */
package org.cagrid.installer.component;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.Constants;
import org.cagrid.installer.steps.PropertyConfigurationStep;
import org.cagrid.installer.steps.RunTasksStep;
import org.cagrid.installer.steps.options.BooleanPropertyConfigurationOption;
import org.cagrid.installer.tasks.ConditionalTask;
import org.cagrid.installer.tasks.installer.ConfigureTomcatTask;
import org.cagrid.installer.tasks.installer.DeployGlobusToTomcatTask;
import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.Condition;


/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 */
public class TomcatComponentInstaller extends AbstractDownloadedComponentInstaller {

    /**
	 * 
	 */
    public TomcatComponentInstaller() {

    }


    /*
     * (non-Javadoc)
     * @see
     * org.cagrid.installer.AbstractExternalComponentInstaller#getComponentId()
     */
    @Override
    protected String getComponentId() {
        return "tomcat";
    }


    @Override
    public void addCheckInstallSteps(CaGridInstallerModel model) {
        // TODO Auto-generated method stub
        super.addCheckInstallSteps(model);

        PropertyConfigurationStep checkDeployGlobusStep = new PropertyConfigurationStep(model
            .getMessage("globus.check.redeploy.title"), model.getMessage("globus.check.redeploy.desc"));
        checkDeployGlobusStep.getOptions().add(
            new BooleanPropertyConfigurationOption(Constants.REDEPLOY_GLOBUS, model.getMessage("yes"), false, false));
        model.add(checkDeployGlobusStep, new Condition() {

            public boolean evaluate(WizardModel m) {
                CaGridInstallerModel model = (CaGridInstallerModel) m;
                return model.isTomcatContainer() && model.isGlobusDeployed() && model.isConfigureContainerSelected() && !model.isTrue(Constants.REINSTALL_TOMCAT);
            }

        });
    }


    /*
     * (non-Javadoc)
     * @seeorg.cagrid.installer.AbstractExternalComponentInstaller#
     * getShouldCheckCondition()
     */
    @Override
    protected Condition getShouldCheckReinstallCondition() {
        return new Condition() {
            public boolean evaluate(WizardModel m) {
                CaGridInstallerModel model = (CaGridInstallerModel) m;
                return model.isTomcatContainer() && model.isTomcatInstalled() && model.isTrue(Constants.INSTALL_CONFIGURE_CONTAINER);
            }
        };
    }


    protected Condition getShouldInstallCondition() {
        return new Condition() {
            public boolean evaluate(WizardModel m) {
                CaGridInstallerModel model = (CaGridInstallerModel) m;
                return model.isTomcatContainer()
                    && (!model.isTomcatInstalled() || model.isTrue(Constants.REINSTALL_TOMCAT));
            }
        };
    }


    @Override
    public void addInstallDownloadedComponentTasks(CaGridInstallerModel model, RunTasksStep deployContainer) {
        // TODO Auto-generated method stub
        super.addInstallDownloadedComponentTasks(model, deployContainer);

        deployContainer.getTasks().add(
            new ConditionalTask(new DeployGlobusToTomcatTask(model.getMessage("deploying.globus.tomcat.title"), model
                .getMessage("deploying.globus.tomcat.title")), new Condition() {

                public boolean evaluate(WizardModel m) {
                    CaGridInstallerModel model = (CaGridInstallerModel) m;
                    return model.isTomcatContainer() && model.isDeployGlobusRequired()
                        && model.isConfigureContainerSelected();
                }

            }));

        deployContainer.getTasks().add(
            new ConditionalTask(new ConfigureTomcatTask(model.getMessage("configuring.tomcat.title"), model
                .getMessage("configuring.tomcat.title")), new Condition() {

                public boolean evaluate(WizardModel m) {
                    CaGridInstallerModel model = (CaGridInstallerModel) m;
                    return model.isTomcatContainer() && model.isConfigureContainerSelected();
                }

            }));
    }

}
