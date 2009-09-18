/**
 * 
 */
package org.cagrid.installer.steps;

import javax.swing.Icon;

import org.cagrid.installer.model.CaGridInstallerModel;
import org.cagrid.installer.steps.options.BooleanPropertyConfigurationOption;
import org.pietschy.wizard.WizardModel;

/**
 * @author <a href="joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class CheckReInstallStep extends PropertyConfigurationStep {

	private String homeProp;
	private String installProp;

	private boolean setSummary;

	/**
	 * 
	 */
	public CheckReInstallStep() {

	}

	/**
	 * @param name
	 * @param description
	 */
	public CheckReInstallStep(String name, String description, String homeProp, String installProp) {
		this(name, description, homeProp, installProp, null);
		
	}

	/**
	 * @param name
	 * @param description
	 * @param icon
	 */
	public CheckReInstallStep(String name, String description, String homeProp, String installProp,
			Icon icon) {
		super(name, description, icon);
		this.homeProp = homeProp;
		this.installProp = installProp;
	}
	
	public void init(WizardModel m){
		CaGridInstallerModel model = (CaGridInstallerModel)m;
		getOptions().add(
				new BooleanPropertyConfigurationOption(installProp, model
						.getMessage("yes"), false, false));
		super.init(m);
	}

	public void prepare() {
		if (!this.setSummary) {
			setSummary(getSummary() + " ("
					+ this.model.getMessage("installed.at") + " '"
					+ this.model.getProperty(this.homeProp) + "')");
			this.setSummary = true;
		}
	}

}
