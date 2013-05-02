/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
