/**
 * 
 */
package org.cagrid.installer.steps;

import javax.swing.Icon;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.options.FilePropertyConfigurationOption;
import org.cagrid.installer.validator.CreateFilePermissionValidator;
import org.pietschy.wizard.WizardModel;

/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class InstallInfoStep extends PropertyConfigurationStep {

	private String installDirPathProp;

	/**
	 * 
	 */
	public InstallInfoStep() {

	}

	/**
	 * @param name
	 * @param description
	 */
	public InstallInfoStep(String name, String description, String installDirPathProp) {
		this(name, description, installDirPathProp, null);

	}

	/**
	 * @param name
	 * @param description
	 * @param icon
	 */
	public InstallInfoStep(String name, String description, String installDirPathProp, Icon icon) {
		super(name, description, icon);
		this.installDirPathProp = installDirPathProp;
	}

	public void init(WizardModel m) {
		CaGridInstallerModel model = (CaGridInstallerModel) m;

		FilePropertyConfigurationOption fpo = new FilePropertyConfigurationOption(
				installDirPathProp, model.getMessage("directory"), System
						.getProperty("user.home"), true);
		fpo.setDirectoriesOnly(true);
		fpo.setBrowseLabel(model.getMessage("browse"));
		getOptions().add(fpo);
		getValidators().add(
				new CreateFilePermissionValidator(installDirPathProp, model
						.getMessage("error.permission.directory.create")));

		super.init(m);
	}

}
