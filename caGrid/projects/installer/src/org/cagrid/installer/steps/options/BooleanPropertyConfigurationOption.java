/**
 * 
 */
package org.cagrid.installer.steps.options;


/**
 * @author <a href="mailto:joshua.phillips@semanticbits.com">Joshua Phillips</a>
 * 
 */
public class BooleanPropertyConfigurationOption extends
		AbstractPropertyConfigurationOption {

	private boolean defaultValue;

	public BooleanPropertyConfigurationOption() {

	}
	
	public BooleanPropertyConfigurationOption(String name, String description,
			boolean defaultValue) {
		super(name, description, false);
		this.defaultValue = defaultValue;
	}

	public BooleanPropertyConfigurationOption(String name, String description,
			boolean defaultValue, boolean required) {
		super(name, description, required);
		this.defaultValue = defaultValue;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

}
